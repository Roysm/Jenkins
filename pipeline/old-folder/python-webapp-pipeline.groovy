pipeline {
    agent any
    environment {
        dockerhub = credentials('dockerhub')
        gitRepoUrl = 'https://github.com/Roysm/Jenkins.git'
        gitRev = 'main'
        credsId = 'a2e4c8bb-dce7-4faa-b10e-3def9ee6ea03'
        dockerfilePath = "webapp/Dockerfile"
        dockerImage = "${dockerhub_USR}/web-app"
        dockerTag = "latest"
        dockerImageFullName = "${dockerImage}:${dockerTag}"
    }
    stages {
        stage('Clone repository') {
            steps {
                script {
                    echo "Cloning \n  Repo: ${gitRepoUrl}\n  Revision: ${gitRev}\n  CredsId: ${credsId}\nto dir: \n  ${WORKSPACE}"
                    git(url: gitRepoUrl, branch: gitRev, credentialsId: credsId)
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
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
