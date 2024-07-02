pipeline {
    agent any
    environment {
        dockerhub = credentials('dockerhub')
        dockerfilePath = "webapp/Dockerfile"
        dockerImage = "${dockerhub_USR}/web-app"
        dockerTag = "latest"
        dockerImageFullName = "${dockerImage}:${dockerTag}"
    }
    stages {
        stage('Hello') {
            steps {
                script {
                    echo 'Hello, World!'
                }
            }
        }
    }
    
    stage('Docker Build') {
            steps {
                script {
                    echo "Docker Build: \n  File: ${dockerfilePath}\n  Image: ${dockerImageFullName}\n  Context: ${WORKSPACE}"
                    sh "docker build -f ${dockerfilePath} -t ${dockerImageFullName} ."
                }
            }
        }
    
    stage('Docker Push') {
            steps {
                script {
                    echo '----------- Login Docker Hub -----------'
                    sh "echo ${dockerhub_PSW} | docker login -u ${dockerhub_USR} --password-stdin"
                    echo '----------- Pushing to Docker Hub -----------'
                    sh "docker push ${dockerImageFullName}"
                }
            }
        }
}
