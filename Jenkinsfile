pipeline {
    agent any

    environment {
        // Example environment variables - define these in Jenkins or replace here
        SONAR_KEY = 'your-sonar-project-key'
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_TOKEN = credentials('sonar-token-id')  // Jenkins credential ID storing SonarQube token
    }

    stages {
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''#!/bin/bash
                        set -e
                        echo "Starting SonarQube scan..."
                        docker run --rm \\
                            -v "${WORKSPACE}:/usr/src" \\
                            -w /usr/src \\
                            sonarsource/sonar-scanner-cli \\
                            -Dsonar.projectKey=${SONAR_KEY} \\
                            -Dsonar.sources=demo-app/ \\
                            -Dsonar.host.url=${SONAR_HOST_URL} \\
                            -Dsonar.login=${SONAR_TOKEN} \\
                            -Dsonar.exclusions="**/node_modules/**,**/target/**,**/.git/**"
                        echo "SonarQube scan finished."
                    '''
                }
            }
        }
    }
}

