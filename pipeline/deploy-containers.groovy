pipeline {
    agent any
    stages {
        stage('Docker-Compose UP') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        echo '----------- Docker-Compose Up -----------'
                        sh "docker-compose -f docker-compose-python-web-app.yaml up -d"
                    }
                }
            }
        }

        stage('HealthCheck') {
            steps {
                script {
                    echo '----------- Checking Python-Web-App Responds OK -----------'
                    timeout(time: 1, unit: 'MINUTES') {
                        sh """
                            echo "Checking connection at:  http://localhost:80"
                            echo "Executing: docker exec nginx-reverse-proxy bash -c 'curl -s -f http://localhost:80'"
                            until docker exec nginx-reverse-proxy bash -c "curl -s -f http://localhost:80"
                            do
                                echo "Sleeping for 5 seconds before re-checking.."
                                sleep 5
                            done
                            echo OK - Connection is OK
                            exit 0
                        """
                    }
                }
            }
        }
    }
}

