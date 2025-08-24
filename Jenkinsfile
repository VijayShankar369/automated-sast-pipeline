pipeline {
  agent any

  environment {
    SONAR_PROJECT_KEY = 'automated-sast-pipeline'
    SONARQUBE_SERVER  = 'SonarQubeServer'
    REPO_URL          = 'https://github.com/VijayShankar369/automated-sast-pipeline.git'
  }

  stages {
    stage('Clone Repository') {
      steps {
        // Clean workspace then clone fresh
        deleteDir()
        git url: "${REPO_URL}", branch: 'main'
      }
    }

    stage('Semgrep Security Scan') {
      steps {
        echo 'Starting Semgrep security scan (native)...'
        sh '''
          semgrep scan \
            --config p/security-audit \
            --config p/secrets \
            --json --output semgrep-results.json \
            --verbose demo-app/
        '''
        archiveArtifacts artifacts: 'semgrep-results.json', allowEmptyArchive: true
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv("${SONARQUBE_SERVER}") {
          echo 'Starting SonarQube scan (native)...'
          sh '''
            sonar-scanner \
              -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
              -Dsonar.sources=demo-app/ \
              -Dsonar.host.url=${SONAR_HOST_URL} \
              -Dsonar.login=${SONAR_AUTH_TOKEN} \
              -Dsonar.exclusions="**/node_modules/**,**/target/**,**/.git/**"
          '''
          echo 'SonarQube scan finished.'
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

  post {
    always  { echo 'Pipeline completed' }
    success { echo 'Pipeline succeeded!' }
    failure { echo 'Pipeline failed!' }
  }
}
