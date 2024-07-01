pipeline {
    agent any
    environment {
		dockerhub = credentials('dockerhub')
        gitRepoUrl = 'https://github.com/RoySCV/Jenkins.git'
        gitRev = 'main'
        credsId = 'github-user-and-access-token'
        dockerfilePath = "Dockerfile"
        dockerImage = "${dockerhub_USR}/nginx-reverse-proxy"
        dockerTag = "1.23.3-python-web-app"
        dockerImageFullName = "${dockerImage}:${dockerTag}"
	}

    stages {
        stage('Clone repository') {
            steps {
                echo "Cloning \n  Repo: ${gitRepoUrl}\n  Revision: ${gitRev}\n  CredsId: ${credsId}\nto dir: \n  ${WORKSPACE}"
                git(url: gitRepoUrl, branch: gitRev, credentialsId: credsId)
            }
        }

        stage('Docker Build') {
            steps {
                dir('nginx-reverse-proxy') {
                    echo "Docker Build: \n  File: ${dockerfilePath}\n  Image: ${dockerImageFullName}\n  Context: ${WORKSPACE}"
                    sh "docker build -f ${dockerfilePath} -t ${dockerImageFullName} ."
                }
            }
        }

        stage('Docker Push') {
            steps {
                    echo '----------- Login Docker Hub -----------'
                    sh "echo ${dockerhub_PSW} | docker login -u ${dockerhub_USR} --password-stdin"
                    echo '----------- Pushing to Docker Hub -----------'
                    sh "docker push ${dockerImageFullName}"
            }
        }

    }
}