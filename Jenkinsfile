pipeline {
    agent any

    environment {
        SONAR_PROJECT_KEY = 'your-sonar-project-key'
        SONARQUBE_SERVER = 'SonarQubeServer'
    }

    stages {
        stage('SonarQube Analysis') {
            steps {
                script {
                    // Use the correct tool identifier for SonarQube Scanner
                    def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                    
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
