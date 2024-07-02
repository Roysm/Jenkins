pipeline {
    agent any
    environment {
        dockerfilePath = "webapp/Dockerfile"
        dockerTag = "latest"
    }
    stages {
        stage('Setup') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USR', passwordVariable: 'DOCKERHUB_PSW')]) {
                        env.DOCKER_IMAGE = "${DOCKERHUB_USR}/web-app"
                        env.DOCKER_IMAGE_FULL_NAME = "${env.DOCKER_IMAGE}:${dockerTag}"
                    }
                }
            }
        }
        stage('Docker Build') {
            steps {
                script {
                    echo "Docker Build: \n  File: ${dockerfilePath}\n  Image: ${env.DOCKER_IMAGE_FULL_NAME}\n  Context: ${WORKSPACE}"
                    // Using Docker plugin to build the image
                    docker.build(env.DOCKER_IMAGE_FULL_NAME, "-f ${dockerfilePath} ${WORKSPACE}")
                }
            }
        }
        stage('Docker Push') {
            steps {
                script {
                    echo '----------- Pushing to Docker Hub -----------'
                    // Using Docker plugin to push the image with automatic login
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        docker.image(env.DOCKER_IMAGE_FULL_NAME).push(dockerTag)
                    }
                }
            }
        }
    }
}
