def gv

pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    environment {
        DOCKER_REPO_SERVER = '664574038682.dkr.ecr.eu-west-3.amazonaws.com'
        DOCKER_REPO = "${DOCKER_REPO_SERVER}/java-maven-app"
    }
    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("increment version") {
            steps {
                script {
                    echo "incrementing app version..."
                    sh "mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} versions:commit"
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                }
            }
        }
        stage("build jar") {
            steps {
                script {
                    gv.buildJar()
                }
            }
        }
        stage("build image") {
            steps {
                script {
                    gv.buildImage()
                }
            }
        }
        stage('deploy') {
            environment {
               AWS_ACCESS_KEY_ID = credentials('jenkins_aws_access_key_id')
               AWS_SECRET_ACCESS_KEY = credentials('jenkins_aws_secret_access_key_id')
               APP_NAME = 'java-maven-app'
            }
            steps {
                script {
                   echo 'deploying docker image...'
                   sh 'envsubst < k8s/deployment.yaml | kubectl apply -f -'
                   sh 'envsubst < k8s/service.yaml | kubectl apply -f -'

                }
            }
        }
        stage("commit version update") {
            steps {
                script {
                    sshagent(credentials: ['github-credentials']) {
                        // Need to set this once
                        // Can also ssh into Jenkins server to set it
                        // sh 'git config --global user.email "jenkins@example.com"'
                        // sh 'git config --global user.name "jenkins"'

                        // sh "git status"
                        // sh "git branch"
                        // sh "git config --list"

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