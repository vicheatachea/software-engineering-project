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
        FULL_APP_IMAGE_NAME = "${DOCKERHUB_APP_REPO}:${DOCKER_IMAGE_TAG}"
        FULL_DB_IMAGE_NAME = "${DOCKERHUB_DB_REPO}:${DOCKER_IMAGE_TAG}"
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
		stage('Build Docker Images') {
			steps {
				// Build Docker image
				script {
					sh "docker buildx build --platform linux/amd64,linux/arm64 -t ${FULL_APP_IMAGE_NAME} -f Dockerfile ."
				}
                script {
                    sh "docker buildx build --platform linux/amd64,linux/arm64 -t ${FULL_DB_IMAGE_NAME} -f Dockerfile-db ."
                }
			}
		}
	
		stage('Push Docker Images') {
			steps {
				// Push both Docker images to Docker Hub
				script {
					docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
						docker.image(env.FULL_APP_IMAGE_NAME).push()
                        docker.image(env.FULL_DB_IMAGE_NAME).push()
          			}
        		}
      		}
    	}
	}
}