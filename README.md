# Jenkins-Assignment
The task will require the following items 
a Jenkins groovy file that creates jobs ( look for job DSL plugin ). 
Items : 
 
1. Jenkins groovy file creates a pipeline job that pulls code from your GitHub repo. Build a docker container and push it to the docker hub.  The docker it builds is a python ( flask simple web application that talks to the local docker engine and gets the list of running containers ) 
2. Another job that takes a default Nginx docker file and modifies it and pushes a proxy pass to the first container (and injects in the request headers a source IP ) then push the container to docker hub
3. A third job that runs the two containers and exposes the Nginx container ports only on the local Jenkins machine then sends a request to verify the  request has gone ok  and finishes successfully

## Getting started

##### In Github, Jenkins:
By this instructions: 
https://secops-sandbox-documentation.readthedocs.io/en/latest/jenkins_github_integration.html
1. Create a Github access token for Jenkins to clone this repository
2. Create Access Token, use the user name and password you get to login jenkins

##### In Docker Hub:

1. Create a Docker Hub credentials with id: `docker-hub-user-and-access-token` of type **username & password** and put your docker hub username & password

## Run

Execute deploy/deploy.bat
