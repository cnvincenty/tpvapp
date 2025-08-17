pipeline {

    agent any

    environment {
        BACKEND_DIR   = "backend"
        FRONTEND_DIR  = "frontend"
        DESPLIEGUE_DIR = "c:\\despliegue"
        NSSM_PATH     = "c:\\nssm\\nssm.exe"
    }

    options {
        timestamps()
    }

    stages {
        stage('Descargar de GITHUB') {
            steps{
                git(url:'https://github.com/cnvincenty/tpvapp.git',branch:'main')
                echo "Código descargado de GitHub ..."
            }
        }

        stage('Compilar Backend') {
            steps{
                dir("${env.BACKEND_DIR}") {
                    bat 'mvn clean install -DskipTests'
                }
                echo "Backend compilado ..."
            }
        }

        stage('Construir Backend') {
            steps{
                script {
                    def targetDir = "${env.WORKSPACE}\\${env.BACKEND_DIR}\\target"
                    def result = bat(
                        script: 'dir /B "${targetDir}\\tpvapp.jar"',
                        returnStdout: true
                    ).trim()
                    if (!result) {
                        error "No se encontró el tpvapp.jar en ${targetDir}"
                    }
                    env.JAR_PATH = "${targetDir}\\${result}"
                    env.JAR_NAME = result
                    println "Backend construido: ${env.JAR_NAME} ..."
                }
            }
        }

        stage('Despliegue Backend') {
            steps {
                script {
                    def despliegueDir = "${env.DESPLIEGUE_DIR}\\backend"
                    bat 'if not exist "${despliegueDir}" mkdir "${despliegueDir}"'
                    bat 'copy /Y "${env.JAR_PATH}" "${despliegueDir}"\\app.jar"'
                    println "Backend Desplegado en ${despliegueDir}\\app.jar ..."
                }
            }
        }

        stage('Servicio Backend') {
            steps {
                script {
                    def servicioName = "tpv-backend"
                    def jarPath = "${env.DESPLIEGUE_DIR}\\backend\\app.jar"
                    def nssmPath = env.NSSM_PATH

                    def run = { cmd ->
                        def proc = new ProcessBuilder(cmd)
                            .redirectErrorStream(true)
                            .start()
                        proc.inputStream.eachLine { println it }
                        proc.waitFor()
                        return proc.exitValue()
                    }

                    def existe = run(["powershell", "-Command", "Get-Service $servicioName -ErrorAction SilentlyContinue"]) == 0


                    if (!existe) {
                        println "Servicio $servicioName no existe. Instalando..."
                        run([nssmPath, "install", servicioName, "java", "-jar", jarPath])
                        run([nssmPath, "set", servicioName, "Start", "SERVICE_AUTO_START"])
                        println "Servicio $servicioName creado e instalado"
                    } else {
                        println "Servicio $servicioName existe. Deteniendo..."
                        run([nssmPath, "stop", servicioName])
                        sleep 5
                    }

                    println "Iniciando servicio $servicioName..."
                    run([nssmPath, "start", servicioName])
                    println "Servicio $servicioName iniciado"
                }
            }
        }
    }

    post {
        always {
            echo 'Proceso de despliegue del backend y frontend completado.'
        }
        failure {
            echo 'El despliegue falló, revisa los logs para más detalles.'
        }
    }

}