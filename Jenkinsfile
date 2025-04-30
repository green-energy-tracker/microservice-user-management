pipeline {
	agent {
        kubernetes {
          label 'jenkins-minikube-template-pod1'
          serviceAccount 'jenkins-agent'
          defaultContainer 'jnlp'
          yaml """
    apiVersion: v1
    kind: Pod
    metadata:
      labels:
        jenkins-jenkins-agent: 'true'
    spec:
      serviceAccountName: jenkins-agent
      containers:
        - name: jnlp
          image: jenkins/inbound-agent:3301.v4363ddcca_4e7-3
          imagePullPolicy: Always
          command:
            - \${computer.jnlpmac}
            - \${computer.name}
          tty: true
          workingDir: /home/jenkins/agent
          env:
            - name: JENKINS_URL
              value: http://jenkins.green-energy-tracker.svc.cluster.local:8080/
        - name: kubectl
          image: alpine/k8s:1.27.3
          imagePullPolicy: Always
          command:
            - cat
          args:
            - "0"
          tty: true
          workingDir: /home/jenkins/agent
      dnsPolicy: ClusterFirst
      restartPolicy: Never
      imagePullSecrets:
        - name: your-image-pull-secret
    """
          yamlMergeStrategy 'override'
          podRetention 'Never'
        }
      }


    tools {
		maven 'M3'
    }

    environment {
		SONARQUBE_SCANNER_HOME = tool 'SonarQubeScanner'
        IMAGE_NAME = 'user-management'
        IMAGE_TAG = 'latest'
        GROUP_ID = 'com.green.energy.tracker'
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
				withSonarQubeEnv('SonarQube') {
                    sh "${SONARQUBE_SCANNER_HOME}/bin/sonar-scanner -Dsonar.projectKey=${IMAGE_NAME} -Dsonar.sources=src -Dsonar.java.binaries=target/classes"
                }
            }
        }

        stage('Build Image') {
            steps {
                withMaven(mavenSettingsConfig: 'nexus-settings') {
                    sh 'mvn jib:build -DsendCredentialsOverHttp=true'
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