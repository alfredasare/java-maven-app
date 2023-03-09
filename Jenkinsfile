#!/usr/bin/env groovy

@Library('jenkins-shared-library')_
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
                    buildImage "alfredasare/devops-demo-app:jma-3.0"
                    dockerLogin()
                    dockerPush 'alfredasare/devops-demo-app:jma-3.0'
                }
            }
        }
        stage("deploy") {
            when {
                expression {
                    BRANCH_NAME == 'master'
                }
            }
            steps {
                script {
                    echo "Deploying the application..."
                    gv.deployApp()
                }
            }
        }
    }   
}