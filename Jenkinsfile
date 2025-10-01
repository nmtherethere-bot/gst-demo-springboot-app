pipeline {
    agent {
        docker {
            // Maven + JDK agent for builds
            image 'maven:3.9.2-eclipse-temurin-17'
        }
    }

    environment {
        DOCKER_REGISTRY = 'nilk3391'
    }

    options {
        // Clean workspace before starting
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
                                sh 'mvn clean package -DskipTests=false'
                            }
                        }]
                    }
                    parallel buildStages
                }
            }
        }

        stage('Docker Check & Login') {
            steps {
                // Ensure Docker CLI works inside Jenkins container
                sh 'docker --version'
                sh 'docker info'

                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
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
                            sh "docker build -t $DOCKER_REGISTRY/${service}:latest ./${service}"
                            echo "Pushing Docker image for ${service}..."
                            sh "docker push $DOCKER_REGISTRY/${service}:latest"
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
