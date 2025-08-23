pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
        skipDefaultCheckout()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = bat(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {  // 👈 must match name in Jenkins > Manage Jenkins > SonarQube servers
                    bat """
                        sonar-scanner ^
                          -Dsonar.projectKey=%JOB_NAME% ^
                          -Dsonar.sources=demo-app/ ^
                          -Dsonar.host.url=%SONAR_HOST_URL% ^
                          -Dsonar.login=%SONAR_AUTH_TOKEN% ^
                          -Dsonar.exclusions=**/node_modules/**,**/target/**,**/.git/**
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed!'
        }
        success {
            echo '✅ All security checks passed!'
        }
        failure {
            echo '❌ Security issues found - build failed!'
        }
    }
}
