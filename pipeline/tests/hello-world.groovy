pipeline {
    agent any
    environment {
        dockerfilePath = "webapp/Dockerfile"
        dockerTag = "latest"
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
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USR', passwordVariable: 'DOCKERHUB_PSW')]) {
                        def dockerImage = "${DOCKERHUB_USR}/web-app"
                        def dockerImageFullName = "${dockerImage}:${dockerTag}"
                        echo '----------- Docker Build -----------'
                        echo "Docker Build: \n  File: ${dockerfilePath}\n  Image: ${dockerImageFullName}\n  Context: ${WORKSPACE}"
                        sh "docker build -f ${dockerfilePath} -t ${dockerImageFullName} ."
                    }
                }
            }
        }
    
        stage('Docker Push') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USR', passwordVariable: 'DOCKERHUB_PSW')]) {
                        def dockerImage = "${DOCKERHUB_USR}/web-app"
                        def dockerImageFullName = "${dockerImage}:${dockerTag}"
                        echo '----------- Login Docker Hub -----------'
                        sh "echo ${DOCKERHUB_PSW} | docker login -u ${DOCKERHUB_USR} --password-stdin"
                        echo '----------- Pushing to Docker Hub -----------'
                        sh "docker push ${dockerImageFullName}"
                    }
                }
            }
        }
    }
}
