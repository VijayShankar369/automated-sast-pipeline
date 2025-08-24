pipeline {
    agent any

    environment {
        SONAR_KEY = 'your-sonar-project-key'
        SONARQUBE_SERVER = 'SonarQubeServer'  // Jenkins SonarQube server name
    }

    stages {
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_SERVER}") {
                    sh '''
                        echo "Starting SonarQube scan..."
                        docker run --rm \
                            -v "${WORKSPACE}:/usr/src" \
                            -w /usr/src \
                            sonarsource/sonar-scanner-cli \
                            -Dsonar.projectKey=${SONAR_KEY} \
                            -Dsonar.sources=demo-app/ \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.login=${SONAR_AUTH_TOKEN} \
                            -Dsonar.exclusions="**/node_modules/**,**/target/**,**/.git/**"
                        echo "SonarQube scan finished."
                    '''
                }
            }
        }
    }
}
