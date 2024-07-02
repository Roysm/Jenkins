pipeline {
    agent any
    parameters {
        string(name: 'DOCKERFILE_PATH', defaultValue: 'nginx-reverse-proxy/Dockerfile', description: 'Path to the Dockerfile')
        string(name: 'BUILD_CONTEXT', defaultValue: 'nginx-reverse-proxy', description: 'Build context directory')
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
                    echo "Docker Build: \n  File: ${params.DOCKERFILE_PATH}\n  Context: ${params.BUILD_CONTEXT}\n  Image: ${env.DOCKER_IMAGE_FULL_NAME}\n  Workspace: ${WORKSPACE}"
                    // Using Docker plugin to build the image
                    docker.build(env.DOCKER_IMAGE_FULL_NAME, "-f ${params.DOCKERFILE_PATH} ${params.BUILD_CONTEXT}")
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
