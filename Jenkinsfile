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
