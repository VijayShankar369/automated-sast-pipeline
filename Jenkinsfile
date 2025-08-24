pipeline {
    agent any
    
    options {
        skipDefaultCheckout()
        timeout(time: 30, unit: 'MINUTES')
    }
    
    stages {
        stage('Checkout Code') {
            steps {
                echo "Checking out code..."
                deleteDir()
                git url: 'https://github.com/VijayShankar369/automated-sast-pipeline.git', branch: 'main'
                echo "Code checked out successfully!"
            }
        }
        
        stage('Test') {
            steps {
                echo "Testing pipeline..."
                sh 'ls -la'
                sh 'echo "Pipeline is working!"'
            }
        }
    }
    
    post {
        always { echo 'Pipeline completed!' }
        success { echo 'SUCCESS!' }
        failure { echo 'FAILED!' }
    }
}

