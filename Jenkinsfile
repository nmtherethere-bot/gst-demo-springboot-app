pipeline {
    agent {
        docker {
            // Maven + JDK agent for builds
            image 'maven:3.8.8-eclipse-temurin-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        DOCKER_REGISTRY = 'nilk3391'
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo 'Checking out code from Git...'
                git branch: 'main',
                    credentialsId: 'git-credentials', // Add your Git token in Jenkins
                    url: 'https://github.com/nmtherethere-bot/gst-demo-springboot-app.git'
            }
        }

        stage('Build & Test Microservices') {
            steps {
                script {
                    def services = ['user-service','invoice-service','return-service','auth-service','api-gateway','eureka-server']
                    services.each { service ->
                        dir(service) {
                            echo "Building and testing ${service}..."
                            sh 'mvn clean package -DskipTests=false'
                        }
                    }
                }
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def services = ['user-service','invoice-service','return-service','auth-service','api-gateway','eureka-server']
                    services.each { service ->
                        sh """
                            docker build -t $DOCKER_REGISTRY/${service}:latest ./${service}
                            docker push $DOCKER_REGISTRY/${service}:latest
                        """
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    def services = ['user-service','invoice-service','return-service','auth-service','api-gateway','eureka-server']
                    services.each { service ->
                        sh "kubectl apply -f k8s/${service}-deployment.yaml"
                    }
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
