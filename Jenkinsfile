pipeline {
    agent any

    environment {
        SONAR_PROJECT_KEY = 'your-sonar-project-key'
        SONARQUBE_SERVER = 'SonarQubeServer'
    }

    tools {
        // Define SonarQube Scanner tool if installed via Global Tool Configuration
        // Remove this section if sonar-scanner is available in PATH
        sonarQubeScanner 'SonarQube Scanner'
    }

    stages {
        stage('SonarQube Analysis') {
            steps {
                script {
                    // Get scanner home path
                    def scannerHome = tool 'SonarQube Scanner'
                    
                    withSonarQubeEnv("${SONARQUBE_SERVER}") {
                        sh """
                            echo "Starting SonarQube scan..."
                            ${scannerHome}/bin/sonar-scanner \
                              -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                              -Dsonar.sources=demo-app/ \
                              -Dsonar.host.url=${SONAR_HOST_URL} \
                              -Dsonar.login=${SONAR_AUTH_TOKEN} \
                              -Dsonar.exclusions="**/node_modules/**,**/target/**,**/.git/**"
                            echo "SonarQube scan finished."
                        """
                    }
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}


