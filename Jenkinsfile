pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    sh '''
                        sonar-scanner \
                          -Dsonar.projectKey=my-project \
                          -Dsonar.sources=. \
                          -Dsonar.host.url=http://<YOUR_SONARQUBE_SERVER>:9000 \
                          -Dsonar.login=<YOUR_TOKEN>
                    '''
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}
