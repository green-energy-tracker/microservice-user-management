pipeline {
	agent any

    tools {
		maven 'M3'
    }

    environment {
		SONARQUBE_SCANNER_HOME = tool 'SonarQubeScanner'
        IMAGE_NAME = 'user-management'
        IMAGE_TAG = 'latest'
        REGISTRY = 'registry.kube-system.svc.cluster.local:80'
    }

    stages {
		stage('Checkout') {
			steps {
				checkout scm
            }
        }

        stage('Build') {
			steps {
				sh 'mvn clean package'
            }
        }

        stage('SonarQube Analysis') {
			steps {
				withSonarQubeEnv('SonarQube') { // Nome del server SonarQube configurato in Jenkins
                    sh "${SONARQUBE_SCANNER_HOME}/bin/sonar-scanner -Dsonar.projectKey=${IMAGE_NAME} -Dsonar.sources=src -Dsonar.java.binaries=target/classes"
                }
            }
        }
        stage('Docker Image Build with Jib') {
			steps {
				sh """
                    mvn compile com.google.cloud.tools:jib-maven-plugin:3.4.1:build \
                      -Dimage=${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}
                    """
            }
        }

        stage('Deploy on Minikube') {
			steps {
				container('kubectl') {
					sh 'kubectl apply -f src/main/resources/k8s/deployment.yaml'
                }
            }
        }
    }

    post {
		success {
			echo 'Build completata con successo!'
        }
        failure {
			echo 'Errore nella build.'
        }
    }
}