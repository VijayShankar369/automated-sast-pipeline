pipeline {
  agent any
  
  options {
    skipDefaultCheckout()
  }
  
  stages {
    stage('Clone Repository') {
      steps {
        deleteDir()
        git url: 'https://github.com/VijayShankar369/automated-sast-pipeline.git', branch: 'main'
      }
    }
    
  stage('Semgrep Scan') {
  steps {
    sh 'semgrep --config=auto demo-app/'
  }
}

    stage('Test Everything Works') {
      steps {
        sh 'echo "Pipeline is working!"'
        sh 'ls -la demo-app/'
      }
    }
  }
  
  post {
    always { echo 'Done!' }
    success { echo 'SUCCESS!' }
    failure { echo 'FAILED!' }
  }
}
