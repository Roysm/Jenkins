pipeline {
    agent any
    environment {
        dockerfilePath = "nginx-reverse-proxy/Dockerfile"
    }
    stages {
        stage('Setup') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USR', passwordVariable: 'DOCKERHUB_PSW')]) {
                        env.DOCKER_IMAGE = "${DOCKERHUB_USR}/nginx-reverse-proxy"
                        env.DOCKER_TAG = "build-${env.BUILD_NUMBER}"
                        env.DOCKER_IMAGE_FULL_NAME = "${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
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
                        docker.image(env.DOCKER_IMAGE_FULL_NAME).push(env.DOCKER_TAG)
                    }
                }
            }
        }
    }
}
