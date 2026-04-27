pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'surbhi2436/task-manager'
    }
    
    stages {
        stage('Submitted by: SURBHI') {
            steps {
                echo '========================================='
                echo 'Student: SURBHI'
                echo 'Topic: Task Management System'
                echo 'Project: DevOps CI/CD Pipeline'
                echo 'Date: 2026-04-27'
                echo '========================================='
            }
        }
        
        stage('Checkout') {
            steps {
                git branch: 'main', 
                    url: 'https://github.com/surbhi2436/task-management-system.git'
            }
        }
        
        stage('Build with Maven') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${DOCKER_IMAGE}:latest .'
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                withCredentials([string(credentialsId: 'docker-hub-password', variable: 'DOCKER_PWD')]) {
                    sh 'echo $DOCKER_PWD | docker login -u surbhi2436 --password-stdin'
                    sh 'docker push ${DOCKER_IMAGE}:latest'
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                sh 'kubectl apply -f k8s/deployment.yaml'
            }
        }
        
        stage('Verify Deployment') {
            steps {
                sh 'kubectl get pods'
                sh 'kubectl get services'
            }
        }
    }
}