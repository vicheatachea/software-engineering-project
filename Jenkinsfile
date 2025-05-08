pipeline {
	agent any
	tools {
		maven 'Maven3'
	}
	environment {
		DOCKERHUB_CREDENTIALS_ID = 'Docker_Hub'
		DOCKERHUB_APP_REPO = 'sakuheinonen/stms'
		DOCKERHUB_DB_REPO = 'sakuheinonen/stms-db'
		DOCKER_IMAGE_TAG = 'latest'
	}
	stages {
		stage('Checkout') {
			steps {
				git branch: 'main', url: 'https://github.com/vicheatachea/student-timetable-management-system.git'
			}
		}
		stage('Build') {
			steps {
				sh 'mvn clean install -DskipTests'
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
		stage('Build App Docker Image') {
			steps {
				// Build Docker image
				script {
					docker.build("${DOCKERHUB_APP_REPO}:${DOCKER_IMAGE_TAG}")
				}
			}
		}

		stage('Build Db Docker Image') {
		    steps {
		    // Build Docker image}
                script {
                    docker.build("${DOCKERHUB_DB_REPO}:${DOCKER_IMAGE_TAG}", "-f Dockerfile-db .")
                }
		    }
		}
	
		stage('Push Docker Images') {
			steps {
				// Push both Docker images to Docker Hub
				script {
					docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
						docker.image("${DOCKERHUB_APP_REPO}:${DOCKER_IMAGE_TAG}").push()
						docker.image("${DOCKERHUB_DB_REPO}:${DOCKER_IMAGE_TAG}").push()
          			}
        		}
      		}
    	}
	}
}