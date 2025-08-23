# Automated SAST Pipeline for Windows

This project implements a complete **Static Application Security Testing (SAST) pipeline** using **Semgrep**, **SonarQube**, **Jenkins**, and **GitHub Actions** on Windows.

## Prerequisites

- Windows 10 version 2004+ or Windows 11
- Docker Desktop for Windows
- Git for Windows
- Python 3.11+
- 8GB RAM (16GB recommended)

## Quick Start

1. Clone this repository
2. Open PowerShell as Administrator
3. Navigate to project directory
4. Run: `docker-compose up -d`
5. Wait 3-5 minutes for services to start
6. Configure SonarQube at http://localhost:9000
7. Configure Jenkins at http://localhost:8080

## Services

- **SonarQube**: http://localhost:9000 (admin/admin)
- **Jenkins**: http://localhost:8080
- **PostgreSQL**: Internal (port 5432)

## Security Coverage

The pipeline detects:
- SQL Injection (CWE-89)
- Hardcoded Credentials (CWE-798)
- Weak Cryptography (CWE-327)
- Secrets in Code (GitHub tokens, AWS keys)
- Command Injection (CWE-78)
- Path Traversal (CWE-22)

## Testing

Test the pipeline with the included vulnerable demo applications in:
- demo-app/java/ - Java vulnerabilities
- demo-app/python/ - Python vulnerabilities  
- demo-app/javascript/ - JavaScript vulnerabilities

## Support

For Windows-specific issues, check Docker Desktop logs and ensure WSL 2 is properly configured.
