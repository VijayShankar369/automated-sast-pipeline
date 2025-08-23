#!/usr/bin/env python3
"""
Deliberately vulnerable Python application for SAST testing
Contains multiple security vulnerabilities for demonstration purposes
"""

import sqlite3
import subprocess
import hashlib

# CWE-798: Hardcoded credentials
DATABASE_PASSWORD = "admin123!"
API_SECRET_KEY = "sk-1234567890abcdef"  
AWS_SECRET_KEY = "AKIAIOSFODNN7EXAMPLE"
DB_CONNECTION_STRING = "postgresql://user:password123@localhost:5432/mydb"
GITHUB_TOKEN = "ghp_1234567890abcdef1234567890abcdef12"

class VulnerableApp:
    def __init__(self):
        self.db_path = "app.db"
        self.secret_key = "hardcoded_secret_key_12345"  # CWE-798
        
    def init_database(self):
        """Initialize database with vulnerable setup"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY,
                username TEXT NOT NULL,
                password TEXT NOT NULL,
                email TEXT
            )
        ''')
        
        cursor.execute("INSERT OR IGNORE INTO users VALUES (1, 'admin', 'admin123', 'admin@example.com')")
        conn.commit()
        conn.close()
    
    def authenticate_user(self, username, password):
        """CWE-89: SQL injection vulnerability"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        # VULNERABLE: Direct string formatting in SQL query
        query = f"SELECT * FROM users WHERE username = '{username}' AND password = '{password}'"
        cursor.execute(query)
        
        result = cursor.fetchone()
        conn.close()
        return result is not None
    
    def get_user_data(self, user_id):
        """Another SQL injection example"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        # VULNERABLE: String concatenation in SQL
        sql = "SELECT username, email FROM users WHERE id = " + str(user_id)
        cursor.execute(sql)
        
        result = cursor.fetchone()
        conn.close()
        return result
    
    def hash_password(self, password):
        """CWE-327: Weak cryptographic algorithm"""
        # VULNERABLE: Using MD5 for password hashing
        md5_hash = hashlib.md5()
        md5_hash.update(password.encode())
        return md5_hash.hexdigest()
    
    def execute_command(self, user_input):
        """CWE-78: OS Command injection"""
        try:
            # VULNERABLE: Direct execution of user input
            result = subprocess.check_output(f"echo {user_input}", shell=True, text=True)
            return result.strip()
        except subprocess.CalledProcessError:
            return "Command execution failed"
    
    def read_file(self, filename):
        """CWE-22: Path traversal vulnerability"""
        try:
            # VULNERABLE: No path validation
            file_path = f"/app/uploads/{filename}"
            with open(file_path, 'r') as file:
                return file.read()
        except FileNotFoundError:
            return "File not found"
        except Exception as e:
            return f"Error: {str(e)}"

def main():
    """Main function for testing vulnerabilities"""
    app_instance = VulnerableApp()
    app_instance.init_database()
    
    print("Testing vulnerable functions:")
    
    # Test SQL injection
    print("1. SQL Injection test:")
    auth_result = app_instance.authenticate_user("admin' OR '1'='1", "any_password")
    print(f"   Authentication bypass: {auth_result}")
    
    # Test weak hashing
    print("2. Weak hashing test:")
    weak_hash = app_instance.hash_password("password123")
    print(f"   MD5 hash: {weak_hash}")
    
    # Test command injection
    print("3. Command injection test:")
    cmd_result = app_instance.execute_command("test && echo vulnerable")
    print(f"   Command result: {cmd_result}")

if __name__ == "__main__":
    main()
