pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'nilk3391'
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Checking out code from Git...'
                git branch: 'main', url: 'https://github.com/nmtherethere-bot/gst-demo-springboot-app.git' // Replace with your repo URL
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

        stage('Docker Build & Push') {
            steps {
                script {
                    def services = ['user-service','invoice-service','return-service','auth-service','api-gateway','eureka-server']
                    services.each { service ->
                        dir(service) {
                            echo "Building Docker image for ${service}..."
                            sh "docker build -t $DOCKER_REGISTRY/${service}:latest ."
                            echo "Pushing Docker image for ${service}..."
                            sh "docker push $DOCKER_REGISTRY/${se_
