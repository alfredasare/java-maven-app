def buildJar() {
    echo "building the application..."
    sh 'mvn clean package'
} 

def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh "docker build -t alfredasare/devops-demo-app:${IMAGE_NAME} ."
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh "docker push alfredasare/devops-demo-app:${IMAGE_NAME}"
    }
}

def deployApp() {
    echo "deploying the application to EC2"
    def dockerCmd = 'docker run -p 3000:3080 -d alfredasare/devops-demo-app:1.1'
    sshagent(['ec2-server-key']) {
        sh "ssh -o StrictHostKeyChecking=no ec2-user@44.199.228.221 ${dockerCmd}"
    }
} 

return this