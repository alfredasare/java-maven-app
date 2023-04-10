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
        stage('provision server') {
            environment {
                AWS_ACCESS_KEY_ID = credentials('jenkins_aws_access_key_id')
                AWS_SECRET_ACCESS_KEY = credentials('jenkins_aws_secret_access_key_id')
                TF_VAR_env_prefix = 'test'
            }

            steps {
                script {
                    dir('terraform') {
                        sh "terraform init"
                        sh "terraform apply --auto-approve"
                        EC2_PUBLIC_IP = sh(
                            script: "terraform output ec2_public_ip",
                            returnStdout: true
                        ).trim()
                    }
                }
            }
        }
        stage("deploy") {
            environment {
                DOCKER_CREDS = credentials('docker-hub-repo')
            }
            steps {
                script {
                    echo "waiting for EC2 server to initialize"
//                     Can add check to check if instance is initialized
                    sleep(time: 120, unit: "SECONDS")

                    echo 'deploying docker image to EC2...'
                    echo "${EC2_PUBLIC_IP}"

                    def shellCmd = "bash ./server-cmds.sh ${IMAGE_NAME} ${DOCKER_CREDS_USR} ${DOCKER_CREDS_PSW}"
                    def ec2Instance = "ec2-user@${EC2_PUBLIC_IP}"

                    sshagent(['server-ssh-key']) {
                        sh "scp -o StrictHostKeyChecking=no server-cmds.sh ${ec2Instance}:/home/ec2-user"
                        sh "scp -o StrictHostKeyChecking=no docker-compose.yaml ${ec2Instance}:/home/ec2-user"
                        sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} ${shellCmd}"
                    }
                }
            }
        }
//         stage("commit version update") {
//             steps {
//                 script {
//                     sshagent(credentials: ['github-credentials']) {
//                         sh "git remote set-url origin git@github.com:alfredasare/java-maven-app.git"
//                         sh "git add ."
//                         sh 'git commit -m "ci: version bump"'
//                         sh "git push origin HEAD:${BRANCH_NAME}"
//                     }
//                 }
//             }
//         }
    }   
}