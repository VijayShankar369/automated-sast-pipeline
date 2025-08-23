pipeline {
    agent any

    tools {
        sonarScanner 'SonarScanner'   // Must match the name set in Jenkins Global Tool Config
    }

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

        stage('Semgrep Security Scan') {
            steps {
                script {
                    try {
                        bat '''
                            docker run --rm -v "%WORKSPACE%:/src" returntocorp/semgrep:latest ^
                                semgrep scan ^
                                --config p/security-audit ^
                                --config p/secrets ^
                                --config /src/semgrep/custom-rules ^
                                --json ^
                                --output /src/semgrep-results.json ^
                                --verbose ^
                                /src/demo-app
                        '''
                    } catch (Exception e) {
                        echo "Semgrep scan completed with findings: ${e.message}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }

                archiveArtifacts artifacts: 'semgrep-results.json', allowEmptyArchive: true

                script {
                    if (fileExists('semgrep-results.json')) {
                        def results = readJSON file: 'semgrep-results.json'
                        def findings = results.results ?: []
                        def criticalCount = findings.findAll {
                            it.extra?.severity?.toLowerCase() == 'error'
                        }.size()

                        echo "Semgrep Results: ${criticalCount} critical issues, ${findings.size()} total"

                        if (criticalCount > 0) {
                            error("Critical security vulnerabilities found: ${criticalCount}")
                        }
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat '''
                        sonar-scanner ^
                          -Dsonar.projectKey=%JOB_NAME% ^
                          -Dsonar.sources=demo-app/ ^
                          -Dsonar.host.url=%SONAR_HOST_URL% ^
                          -Dsonar.login=%SONAR_AUTH_TOKEN% ^
                          -Dsonar.exclusions=**/node_modules/**,**/target/**,**/.git/**
                    '''
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

