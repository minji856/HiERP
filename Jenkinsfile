pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials') // Jenkins Credentials에 등록
        IMAGE_NAME = 'maymin/hierp'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        EC2_HOST = 'your-ec2-public-ip'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/minji856/HiERP.git'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
            }
        }

        stage('Docker Push') {
            steps {
                sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"
                sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                sh "docker push ${IMAGE_NAME}:latest"
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(credentials: ['ec2-ssh-key']) { // Jenkins Credentials에 등록
                    sh """
                        ssh -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} '
                            cd ~/hierp &&
                            IMAGE_TAG=${IMAGE_TAG} docker compose pull app &&
                            IMAGE_TAG=${IMAGE_TAG} docker compose up -d --no-deps app
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Build & Test & Deploy 성공'
        }
        failure {
            echo 'Build 또는 Test 또는 Deploy 실패'
        }
    }
}