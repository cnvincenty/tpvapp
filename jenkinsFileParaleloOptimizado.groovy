pipeline {
    agent any

    environment {
        BACKEND_DIR    = "backend"
        FRONTEND_DIR   = "frontend"
        DESPLIEGUE_DIR = "c:\\despliegue"
        NSSM_PATH      = "c:\\nssm\\nssm.exe"
        NGINX_SERVICE  = "nginx"
    }

    options {
        timestamps()
    }

    stages {
        stage('Compilaciones en paralelo') {
            parallel {
                stage('Prueba') {
                    steps { script { buildApp("prueba", "tpv-backend-prueba", "tpvprueba") } }
                }
                stage('Preproduccion') {
                    steps { script { buildApp("preproduccion", "tpv-backend-preproduccion", "tpvapppre") } }
                }
                stage('Produccion') {
                    steps { script { buildApp("produccion", "tpv-backend-produccion", "tpvapp") } }
                }
            }
        }

        stage('Despliegues secuenciales') {
            steps {
                script {
                    gestionarServicioBackend("tpv-backend-prueba", "9595")
                    desplegarFrontend("${env.WORKSPACE}\\prueba", "tpvprueba")

                    gestionarServicioBackend("tpv-backend-preproduccion", "9696")
                    desplegarFrontend("${env.WORKSPACE}\\preproduccion", "tpvapppre")

                    gestionarServicioBackend("tpv-backend-produccion", "9797")
                    desplegarFrontend("${env.WORKSPACE}\\produccion", "tpvapp")
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completado.'
        }
        failure {
            echo 'El despliegue falló, revisa los logs para más detalles.'
        }
    }
}

/* ---------- FUNCIONES ---------- */

def buildApp(branch, servicioBackend, carpetaFrontend) {
    echo "=== Iniciando compilación para ${branch} ==="
    def branchDir = "${env.WORKSPACE}\\${branch}"

    dir(branchDir) {
        stage("Checkout ${branch}") {
            git(url: 'https://github.com/cnvincenty/tpvapp.git', branch: branch)
        }

        stage("Backend ${branch}") {
            parallel(
                "Compilar": {
                    dir("${branchDir}\\${env.BACKEND_DIR}") {
                        powershell 'mvn clean install -DskipTests'
                    }
                },
                "Construir": {
                    construirBackend(branchDir, servicioBackend)
                }
            )
        }

        stage("Frontend ${branch}") {
            parallel(
                "Compilar": {
                    dir("${branchDir}\\${env.FRONTEND_DIR}") {
                        powershell 'npm install'
                    }
                },
                "Construir": {
                    dir("${branchDir}\\${env.FRONTEND_DIR}") {
                        powershell 'npm run build'
                    }
                }
            )
        }
    }

    echo "=== Compilación ${branch} completada ==="
}

/* ---------- FUNCIONES AUXILIARES ---------- */

def construirBackend(branchDir, servicioBackend) {
    def targetDir = "${branchDir}\\${env.BACKEND_DIR}\\target"
    def jarFile = powershell(
        script: "Get-ChildItem -Path '${targetDir}' -Filter '*.jar' | Where-Object { \$_.Name -notlike '*.original*' } | Select-Object -First 1 -ExpandProperty Name",
        returnStdout: true
    ).trim()

    if (!jarFile) {
        error "No se encontró un JAR válido en ${targetDir}"
    }

    def jarPath = "${targetDir}\\${jarFile}"
    def despliegueDir = "${env.DESPLIEGUE_DIR}\\${servicioBackend}"

    powershell """
        if (-not (Test-Path '${despliegueDir}')) { New-Item -ItemType Directory -Path '${despliegueDir}' | Out-Null }
        Copy-Item -Path '${jarPath}' -Destination '${despliegueDir}\\app.jar' -Force
    """
    echo "Backend compilado en ${despliegueDir}\\app.jar"
}

def gestionarServicioBackend(servicioBackend, puertoBackend) {
    def jarPath = "${env.DESPLIEGUE_DIR}\\${servicioBackend}\\app.jar"
    def nssmPath = env.NSSM_PATH

    def existe = powershell(
        script: "(Get-Service -Name '${servicioBackend}' -ErrorAction SilentlyContinue) -ne \$null",
        returnStatus: true
    ) == 0

    if (!existe) {
        echo "Instalando servicio ${servicioBackend} ..."
        powershell """
            & '${nssmPath}' install '${servicioBackend}' "java" " -jar '${jarPath}' --server.port=${puertoBackend}"
            & '${nssmPath}' set '${servicioBackend}' Start SERVICE_AUTO_START
        """
    } else {
        echo "Reiniciando servicio ${servicioBackend} ..."
        powershell "& '${nssmPath}' stop '${servicioBackend}'"
        powershell """
            while ((Get-Service -Name '${servicioBackend}').Status -eq 'Running') {
                Start-Sleep -Seconds 2
            }
        """
    }

    powershell "& '${nssmPath}' start '${servicioBackend}'"
    verificarServicio(servicioBackend, puertoBackend)
}

def verificarServicio(servicioBackend, puertoBackend) {
    powershell """
        \$maxWait = 30
        \$count = 0
        do {
            Start-Sleep -Seconds 2
            \$status = (Get-Service -Name '${servicioBackend}').Status
            Write-Output "Estado actual de ${servicioBackend}: \$status"
            \$count++
        } while (\$status -ne 'Running' -and \$count -lt \$maxWait)

        if (\$status -ne 'Running') {
            Write-Error "El servicio ${servicioBackend} no llegó a estado 'Running' después de \$maxWait*2 segundos"
            exit 1
        }
    """
    echo "Servicio ${servicioBackend} iniciado en puerto ${puertoBackend}"
}

def desplegarFrontend(branchDir, carpetaFrontend) {
    powershell """
        if (-not (Test-Path "C:\\nginx\\html\\${carpetaFrontend}")) { New-Item -ItemType Directory -Path "C:\\nginx\\html\\${carpetaFrontend}" | Out-Null }
        Remove-Item -Path "C:\\nginx\\html\\${carpetaFrontend}\\*" -Include *.* -Force -ErrorAction SilentlyContinue
    """

    powershell "& '${env.NSSM_PATH}' stop '${env.NGINX_SERVICE}'"

    def command = "Copy-Item -Path ${branchDir}\\${env.FRONTEND_DIR}\\dist\\partevisual\\browser\\* -Destination C:\\nginx\\html\\${carpetaFrontend}\\ -Recurse -Force"
    powershell(returnStdout:true, script:command)

    powershell "& '${env.NSSM_PATH}' start '${env.NGINX_SERVICE}'"
    echo "Frontend desplegado en carpeta ${carpetaFrontend}"
}
