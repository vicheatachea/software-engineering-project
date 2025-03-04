pipeline {
	agent any
	tools {
		maven 'Maven3'
	}
	stages {
		stage('Checkout') {
			steps {
				git branch: 'controller-tests', url: 'https://github.com/vicheatachea/software-engineering-project.git'
			}
		}
		stage('Build') {
			steps {
				sh 'mvn clean install'
			}
		}
		stage('Test') {
			steps {
				sh 'mvn test'
			}
		}
		stage('Code Coverage') {
			steps {
				sh 'mvn jacoco:report'
			}
		}
		stage('Publish Test Results') {
			steps {
				junit '**/target/surefire-reports/*.xml'
			}
		}
		stage('Publish Coverage Report') {
			steps {
				jacoco()
			}
		}
	}
}