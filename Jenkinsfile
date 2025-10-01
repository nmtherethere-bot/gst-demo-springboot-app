pipeline {
    agent any   // run on Jenkins agent (your Jenkins container)

    tools {
        maven 'maven'     // Maven tool configured in Jenkins global tools
        dockerTool 'docker' // Docker tool configured in Jenkins global tools
    }

    environment {
        DOCKER_REGISTRY = 'nilk3391'
        DOCKER = tool 'docker'   // resolves to installed docker path
        MVN    = tool 'maven'    // resolves to installed maven path
        PATH   = "${DOCKER}/bin:${MVN}/bin:${env.PATH}"
    }

    options {
        skipDefaultCheckout false
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout Code') {
            steps {
                echo 'Checking out code from Git...'
                git branch: 'main',
                    credentialsId: 'git-credentials',
                    url: 'https://github.com/nmtherethere-bot/gst-demo-springboot-app.git'
            }
        }

        stage('Build & Test Microservices') {
            steps {
                script {
                    def services = ['user-service','invoice-service','returns-service','auth-service','api-gateway','eureka-server']
                    def buildStages = services.collectEntries { service ->
                        ["${service}": {
                            dir(service) {
                                echo "Building and testing ${service}..."
                                sh "${MVN}/bin/mvn clean package -DskipTests=false"
                            }
                        }]
                    }
                    parallel buildStages
                }
            }
        }

        stage('Docker Check & Login') {
            steps {
                sh "${DOCKER}/bin/docker --version"
                sh "${DOCKER}/bin/docker info"

                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "echo \$DOCKER_PASS | ${DOCKER}/bin/docker login -u \$DOCKER_USER --password-stdin"
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def services = ['user-service','invoice-service','returns-service','auth-service','api-gateway','eureka-server']
                    def dockerStages = services.collectEntries { service ->
                        ["${service}": {
                            echo "Building Docker image for ${service}..."
                            sh "${DOCKER}/bin/docker build -t $DOCKER_REGISTRY/${service}:latest ./${service}"
                            echo "Pushing Docker image for ${service}..."
                            sh "${DOCKER}/bin/docker push $DOCKER_REGISTRY/${service}:latest"
                        }]
                    }
                    parallel dockerStages
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    def services = ['user-service','invoice-service','returns-service','auth-service','api-gateway','eureka-server']
                    def deployStages = services.collectEntries { service ->
                        ["${service}": {
                            sh "kubectl apply -f k8s-deployment/${service}-deployment.yaml"
                        }]
                    }
                    parallel deployStages
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed! Check the logs above.'
        }
    }
}
