def gv

pipeline {
    agent any
    tools {
        maven 'maven-3.9'
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
        stage("deploy") {
            steps {
                script {
                    echo "Deploying the application..."
                    gv.deployApp()
                }
            }
        }
        stage("commit version update") {
            steps {
                script {
                    sshagent(credentials: ['github-credentials']) {
                        // Need to set this once
                        // Can also ssh into Jenkins server to set it
                        sh 'git config --global user.email "alfredasare101@gmail.com"'
                        sh 'git config --global user.name "alfredasare"'

                        sh "git status"
                        sh "git branch"
                        sh "git config --list"

                        sh "git remote set-url origin git@github.com:alfredasare/java-maven-app.git"
                        sh "git add ."
                        sh 'git commit -m "ci: version bump"'
                        sh "git push origin HEAD:jenkins-jobs"
                    }
                }
            }
        }
    }   
}