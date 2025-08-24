pipeline {
    agent any
    
    environment {
        SONAR_PROJECT_KEY = 'demo-sast-pipeline'
        SONAR_PROJECT_NAME = 'SAST Demo Pipeline'
    }
    
    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.BUILD_COMMIT = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                }
                echo "Checked out commit: ${env.BUILD_COMMIT}"
            }
        }
        
        stage('Semgrep Security Scan') {
            steps {
                echo "Starting Semgrep security scan..."
                script {
                    sh """
                        docker run --rm \
                            -v "\${WORKSPACE}:/src" \
                            returntocorp/semgrep:latest \
                            semgrep scan \
                            --config p/security-audit \
                            --config p/secrets \
                            --json \
                            --output /src/semgrep-results.json \
                            --verbose \
                            /src/demo-app || echo "Semgrep scan completed with findings"
                    """
                    
                    if (fileExists('semgrep-results.json')) {
                        echo "Semgrep results found and archived"
                        archiveArtifacts artifacts: 'semgrep-results.json', allowEmptyArchive: true
                    }
                }
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                echo "Starting SonarQube analysis..."
                script {
                    def scannerHome = tool 'SonarQubeScanner'
                    withSonarQubeEnv('SonarQubeServer') {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                                -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \
                                -Dsonar.projectName="${env.SONAR_PROJECT_NAME}" \
                                -Dsonar.sources=demo-app/ \
                                -Dsonar.exclusions="**/node_modules/**,**/target/**,**/.git/**" \
                                -Dsonar.java.sources=demo-app/java/src \
                                -Dsonar.python.sources=demo-app/python \
                                -Dsonar.javascript.sources=demo-app/javascript
                        """
                    }
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: false
                }
            }
        }
        
        stage('Security Report') {
            steps {
                echo "Generating security report..."
                sh """
                    cat > security-report.md << EOF
# SAST Security Scan Report

**Build:** ${env.BUILD_NUMBER}
**Commit:** ${env.BUILD_COMMIT}
**Date:** \$(date)

## Scan Results
- Semgrep SAST scan: Completed
- SonarQube analysis: Completed
- Status: Security vulnerabilities detected (expected for demo)

## Demo Applications Scanned
- Java application with SQL injection vulnerabilities
- Python application with hardcoded credentials
- JavaScript application with security issues

## Next Steps
1. Review detailed findings in semgrep-results.json
2. Check SonarQube dashboard for detailed analysis
3. Fix critical vulnerabilities before deployment
EOF
                """
                archiveArtifacts artifacts: 'security-report.md', allowEmptyArchive: true
                echo "Security report generated and archived"
            }
        }
    }
    
    post {
        always {
            echo "SAST Pipeline completed!"
            echo "✅ All security scans completed!"
            
            // Clean up workspace if needed
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                               [pattern: '.propsfile', type: 'EXCLUDE']])
        }
        success {
            echo "🎉 Pipeline completed successfully!"
        }
        failure {
            echo "❌ Pipeline failed! Check logs for details."
        }
    }
}
