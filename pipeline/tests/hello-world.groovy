pipeline {
    agent any
    environment {
        dockerfilePath = "webapp/Dockerfile"
        dockerImage = "${DOCKERHUB_USR}/web-app"
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
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USR', passwordVariable: 'DOCKERHUB_PSW')]) {
                    sh "echo ${DOCKERHUB_PSW} | docker login -u ${DOCKERHUB_USR} --password-stdin"
                    echo '----------- Pushing to Docker Hub -----------'
                    sh "docker push ${dockerImageFullName}"
                    }
                }
            }
        }
    }
}
