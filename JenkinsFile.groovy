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
        stage('Descargar de GITHUB') {
            steps {
                git(url:'https://github.com/cnvincenty/tpvapp.git', branch:'main')
                echo "Código descargado de GitHub ..."
            }
        }

        stage('Compilar Backend') {
            steps {
                dir("${env.BACKEND_DIR}") {
                    powershell 'mvn clean install -DskipTests'
                }
                echo "Backend compilado ..."
            }
        }

        stage('Construir Backend') {
            steps {
                script {
                    def targetDir = "${env.WORKSPACE}\\${env.BACKEND_DIR}\\target"

                    def jarFile = powershell(
                        script: "Get-ChildItem -Path '${targetDir}' -Filter '*.jar' | Where-Object { \$_.Name -notlike '*.original*' } | Select-Object -First 1 -ExpandProperty Name",
                        returnStdout: true
                    ).trim()

                    if (!jarFile) {
                        error "No se encontró un JAR válido en ${targetDir}"
                    }

                    env.JAR_PATH = "${targetDir}\\${jarFile}"
                    env.JAR_NAME = jarFile

                    echo "Backend construido: ${env.JAR_NAME} ..."
                }
            }
        }

        stage('Despliegue Backend') {
            steps {
                script {
                    def despliegueDir = "${env.DESPLIEGUE_DIR}\\backend"

                    powershell """
                        if (-not (Test-Path '${despliegueDir}')) { New-Item -ItemType Directory -Path '${despliegueDir}' | Out-Null }
                        Copy-Item -Path '${env.JAR_PATH}' -Destination '${despliegueDir}\\app.jar' -Force
                    """

                    echo "Backend desplegado en ${despliegueDir}\\app.jar ..."
                }
            }
        }

        stage('Servicio Backend') {
            steps {
                script {
                    def servicioName = "tpv-backend"
                    def jarPath = "${env.DESPLIEGUE_DIR}\\backend\\app.jar"
                    def nssmPath = env.NSSM_PATH

                    def existe = powershell(
                        script: "if (Get-Service -Name '${servicioName}' -ErrorAction SilentlyContinue) { exit 0 } else { exit 1 }",
                        returnStatus: true
                    ) == 0

                    if (!existe) {
                        echo "Instalando servicio ${servicioName} ..."
                        powershell "& '${nssmPath}' install '${servicioName}' java -jar '${jarPath}'"
                        powershell "& '${nssmPath}' set '${servicioName}' Start SERVICE_AUTO_START"
                        echo "Servicio ${servicioName} creado e instalado"
                    } else {
                        echo "Reiniciando servicio ${servicioName} ..."
                        powershell "& '${nssmPath}' stop '${servicioName}'"
                        sleep 5
                    }

                    echo "Iniciando servicio ${servicioName} ..."
                    powershell "& '${nssmPath}' start '${servicioName}'"
                    echo "Servicio ${servicioName} iniciado"
                }
            }
        }

        stage('Compilar Frontend') {
            steps {
                dir("${env.FRONTEND_DIR}") {
                    powershell 'npm install'
                }
                echo "Backend compilado ..."
            }
        }

        stage('Construir Frontend') {
            steps {
                dir("${env.FRONTEND_DIR}") {
                    powershell 'npm run build'
                }
                echo "Backend construido ..."
            }
        }

        stage('Limpiar') {
            steps {
                powershell(returnStdout:true, script:'Remove-Item -Path "C:\\nginx\\html\\tpvapp\\*" -Include *.* -Force');
                println('Limpieza');
            }
        }

        stage('Parar servicio nginx') {
            steps {
                script {
                    powershell "& '${env.NSSM_PATH}' stop 'nginx.exe'"
                    println('Parar servicio ...');
                }
            }
        }

        stage('Despliegue de archivos') {
            steps {
                script {
                    String command = "Copy-Item -Path " + "${WORKSPACE}" + "\\Dist\\* -Destination 'C:\\nginx\\html\\tpvapp\\' -Recurse -Force";
                    println(command);
                    powershell(returnStdout:true, script:command);
                    println('Despliegue realizado ...');
                }
            }
        }

        stage('Levantar servicio nginx') {
            steps {
                script {
                    powershell "& '${env.NSSM_PATH}' start 'nginx.exe'"
                    println('Levantar servicio ...');
                }
            }
        }
    }

    post {
        always {
            echo 'Proceso de despliegue completado.'
        }
        failure {
            echo 'El despliegue falló, revisa los logs para más detalles.'
        }
    }
}
