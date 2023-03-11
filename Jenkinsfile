#!/usr/bin/env groovy

// @Library('jenkins-shared-library')_

library identifier: 'jenkins-shared-library@main', retriever: modernSCM(
    [
        $class: 'GitSCMSource',
        remote: 'git@github.com:alfredasare/jenkins-shared-library.git',
        credentialsId: 'github-credentials'
    ]
)

def gv

pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    environment {
        IMAGE_NAME = 'alfredasare/devops-demo-app:java-maven-1.0'
    }
    stages {
//         stage("init") {
//             steps {
//                 script {
//                     gv = load "script.groovy"
//                 }
//             }
//         }
        stage("build jar") {
            steps {
                script {
                    buildJar()
                }
            }
        }
        stage("build and push image") {
            steps {
                script {
                    buildImage(env.IMAGE_NAME)
                    dockerLogin()
                    dockerPush(env.IMAGE_NAME)
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    echo "Deploying the app..."
                    echo "deploying the application to EC2"
                    def shellCmd = "bash ./server-cmds.sh"
                    sshagent(['ec2-server-key']) {
                        sh "scp server-cmds.sh ec2-user@44.199.228.221:/home/ec2-user"
                        sh "scp docker-compose.yaml ec2-user@44.199.228.221:/home/ec2-user"
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@44.199.228.221 ${shellCmd}"
                    }
                }
            }
        }
    }   
}