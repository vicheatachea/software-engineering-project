pipeline {
	agent any
	tools {
		maven 'Maven3'
	}
	environment {
		DOCKERHUB_CREDENTIALS_ID = 'Docker_Hub'
		DOCKERHUB_REPO = 'sakuheinonen/stms'
		DOCKER_IMAGE_TAG = 'latest_v2'
	}
	stages {
		stage('Checkout') {
			steps {
				git branch: 'main', url: 'https://github.com/vicheatachea/software-engineering-project.git'
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
				recordCoverage(
            tools: [[parser: 'JACOCO']],
            sourceCodeRetention: 'EVERY_BUILD',
            qualityGates: [
                [threshold: 60, metric: 'LINE', baseline: 'PROJECT']
					]
				)
			}
		}
		stage('Build Docker Image') {
			steps {
				// Build Docker image
				script {
					docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
				}
			}
		}
		stage('Push Docker Image to Docker Hub') {
			steps {
				// Push Docker image to Docker Hub
				script {
					docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
						docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
					}
				}
			}
		}
	}
}