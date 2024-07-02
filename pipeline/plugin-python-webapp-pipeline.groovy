pipeline {
    agent any
    environment {
        dockerfilePath = "webapp/Dockerfile"
        dockerTag = "latest"
    }
    stages {
        stage('Docker Build') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USR', passwordVariable: 'DOCKERHUB_PSW')]) {
                        def dockerImage = "${DOCKERHUB_USR}/web-app"
                        def dockerImageFullName = "${dockerImage}:${dockerTag}"
                        echo "Docker Build: \n  File: ${dockerfilePath}\n  Image: ${dockerImageFullName}\n  Context: ${WORKSPACE}"
                        // Using Docker plugin to build the image
                        docker.build(dockerImageFullName, "-f ${dockerfilePath} ${WORKSPACE}")
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
                        echo '----------- Pushing to Docker Hub -----------'
                        // Using Docker plugin to push the image with automatic login
                        docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                            docker.image(dockerImageFullName).push(dockerTag)
                        }
                    }
                }
            }
        }
    }
}
