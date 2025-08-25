pipeline {
    agent any

    environment {
        BACKEND_DIR    = "backend"
        FRONTEND_DIR   = "frontend"
        DESPLIEGUE_DIR = "c:\\despliegue"
        NSSM_PATH      = "c:\\nssm\\nssm.exe"
    }

    options {
        timestamps()
    }

    stages {
        stage('Despliegues en paralelo') {
            parallel {
                stage('Despliegue Prueba') {
                    steps {
                        script {
                            despliegueApp("prueba","tpv-backend-prueba","tpvprueba","9595")
                        }
                    }
                }

                stage('Despliegue Preproduccion') {
                    steps {
                        script {
                            despliegueApp("preproduccion","tpv-backend-preproduccion","tpvapppre","9696")
                        }
                    }
                }

                stage('Despliegue Produccion') {
                    steps {
                        script {
                            despliegueApp("produccion","tpv-backend-produccion","tpvapp","9797")
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Proceso de despliegue completado (paralelo).'
        }
        failure {
            echo 'El despliegue falló, revisa los logs para más detalles.'
        }
    }
}

def despliegueApp(branch, servicioBackend, carpetaFrontend, puertoBackend) {
    echo "=== Iniciando despliegue para ${branch} ==="

    def branchDir = "${env.WORKSPACE}\\${branch}"

    dir(branchDir) {
        stage("Checkout ${branch}") {
            git(url: 'https://github.com/cnvincenty/tpvapp.git', branch: branch)
        }

        stage("Compilar Backend ${branch}") {
            dir("${branchDir}\\${env.BACKEND_DIR}") {
                powershell 'mvn clean install -DskipTests'
            }
        }

        stage("Construir Backend ${branch}") {
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
            echo "Backend desplegado en ${despliegueDir}\\app.jar"
        }

        stage("Servicio Backend ${branch}") {
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



        stage("Compilar Frontend ${branch}") {
            dir("${branchDir}\\${env.FRONTEND_DIR}") {
                powershell 'npm install'
            }
        }

        stage("Construir Frontend ${branch}") {
            dir("${branchDir}\\${env.FRONTEND_DIR}") {
                powershell 'npm run build'
            }
        }

        stage("Limpiar Frontend ${branch}") {
            powershell """
                if (-not (Test-Path "C:\\nginx\\html\\${carpetaFrontend}")) { New-Item -ItemType Directory -Path "C:\\nginx\\html\\${carpetaFrontend}" | Out-Null }
                Remove-Item -Path "C:\\nginx\\html\\${carpetaFrontend}\\*" -Include *.* -Force -ErrorAction SilentlyContinue
            """
        }

        stage("Parar nginx ${branch}") {
            powershell "& '${env.NSSM_PATH}' stop 'nginx'"
        }

        stage("Despliegue Frontend ${branch}") {
            def command = "Copy-Item -Path ${branchDir}\\${env.FRONTEND_DIR}\\dist\\partevisual\\browser\\* -Destination C:\\nginx\\html\\${carpetaFrontend}\\ -Recurse -Force"
            powershell(returnStdout:true, script:command)
        }

        stage("Levantar nginx ${branch}") {
            powershell "& '${env.NSSM_PATH}' start 'nginx'"
        }
    }

    echo "=== Despliegue ${branch} completado ==="
}
