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
    stages {
//         stage("init") {
//             steps {
//                 script {
//                     gv = load "script.groovy"
//                 }
//             }
//         }
        stage("increment version") {
            steps {
                script {
                    echo "incrementing app version..."
                    sh "mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} versions:commit"
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "alfredasare/devops-demo-app:java-maven-$version-$BUILD_NUMBER"
                }
            }
        }
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
                    def shellCmd = "bash ./server-cmds.sh ${IMAGE_NAME}"
                    def ec2Instance = "ec2-user@44.199.228.221"

                    sshagent(['ec2-server-key']) {
                        sh "scp server-cmds.sh ${ec2Instance}:/home/ec2-user"
                        sh "scp docker-compose.yaml ${ec2Instance}:/home/ec2-user"
                        sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} ${shellCmd}"
                    }
                }
            }
        }
        stage("commit version update") {
            steps {
                script {
                    sshagent(credentials: ['github-credentials']) {
                        sh "git remote set-url origin git@github.com:alfredasare/java-maven-app.git"
                        sh "git add ."
                        sh 'git commit -m "ci: version bump"'
                        sh "git push origin HEAD:${BRANCH_NAME}"
                    }
                }
            }
        }
    }   
}