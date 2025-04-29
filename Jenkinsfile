pipeline {
	agent any

    tools {
		maven 'M3'
    }

    environment {
		SONARQUBE_SCANNER_HOME = tool 'SonarQubeScanner'
        IMAGE_NAME = 'user-management'
        IMAGE_TAG = 'latest'
        REGISTRY = 'nexus.nexus.svc.cluster.local:5000'
        GROUP_ID = 'com.green.energy.tracker'
        NEXUS_CREDENTIALS_ID = 'nexus-docker-creds'
    }

    stages {
		stage('Checkout') {
			steps {
				checkout scm
            }
        }

        stage('Build package') {
            steps {
                withMaven(mavenSettingsConfig: 'nexus-settings') {
                   sh 'mvn clean package'
                }
            }
        }

        stage('SonarQube Analysis') {
			steps {
				withSonarQubeEnv('SonarQube') { // Nome del server SonarQube configurato in Jenkins
                    sh "${SONARQUBE_SCANNER_HOME}/bin/sonar-scanner -Dsonar.projectKey=${IMAGE_NAME} -Dsonar.sources=src -Dsonar.java.binaries=target/classes"
                }
            }
        }

        stage('Build Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${NEXUS_CREDENTIALS_ID}", usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                    withMaven(mavenSettingsConfig: 'nexus-settings') {
                        sh 'mvn jib:build -DsendCredentialsOverHttp=true'
                    }
                }
            }
        }

        stage('Deploy on Minikube') {
            steps {
                container('kubectl') {
                    script {
                        sh 'kubectl apply -f src/main/resources/k8s/deployment.yaml'

                        def deploymentName = "user-management-pod"

                        echo "Checking if deployment ${deploymentName} exists..."
                        def exists = sh(
                            script: "kubectl get deployment ${deploymentName} --ignore-not-found",
                            returnStdout: true
                        ).trim()

                        if (exists) {
                            echo "Deployment found. Forcing rollout restart..."
                            sh "kubectl rollout restart deployment/${deploymentName}"
                        } else {
                            echo "Deployment not found yet. Skipping rollout restart."
                        }
                    }
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