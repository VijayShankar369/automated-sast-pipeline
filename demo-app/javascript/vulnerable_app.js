/**
 * Deliberately vulnerable Node.js application for SAST testing
 * Contains multiple security vulnerabilities for demonstration purposes
 */

const { exec } = require('child_process');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

// CWE-798: Hardcoded credentials
const DATABASE_PASSWORD = "admin123!";
const API_SECRET_KEY = "sk-1234567890abcdef";
const JWT_SECRET = "hardcoded_jwt_secret_12345";
const AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
const GITHUB_TOKEN = "ghp_1234567890abcdef1234567890abcdef12";

class VulnerableApp {
    constructor() {
        this.secretKey = "another_hardcoded_secret";
    }
    
    /**
     * CWE-89: SQL injection vulnerability
     */
    authenticateUser(username, password) {
        // VULNERABLE: Direct string concatenation in SQL query
        const query = SELECT * FROM users WHERE username = '' AND password = '';
        console.log("Executing query:", query);
        return true; // Simplified for demo
    }
    
    /**
     * Another SQL injection example
     */
    getUserById(userId) {
        // VULNERABLE: No parameterization
        const sql = SELECT username, email FROM users WHERE id = ;
        console.log("SQL Query:", sql);
        return { username: "test", email: "test@example.com" };
    }
    
    /**
     * CWE-327: Weak cryptographic algorithm
     */
    hashPassword(password) {
        // VULNERABLE: Using MD5 for password hashing
        return crypto.createHash('md5').update(password).digest('hex');
    }
    
    /**
     * CWE-78: OS Command injection
     */
    executeSystemCommand(userInput, callback) {
        // VULNERABLE: Direct execution of user input
        const command = echo "";
        
        exec(command, (error, stdout, stderr) => {
            if (error) {
                callback(error, null);
                return;
            }
            callback(null, stdout);
        });
    }
    
    /**
     * CWE-22: Path traversal vulnerability
     */
    readUserFile(filename, callback) {
        // VULNERABLE: No path validation
        const filePath = path.join('/app/user_files/', filename);
        
        fs.readFile(filePath, 'utf8', (error, data) => {
            if (error) {
                callback(error, null);
                return;
            }
            callback(null, data);
        });
    }
    
    /**
     * CWE-94: Code injection via eval
     */
    calculateExpression(expression) {
        try {
            // VULNERABLE: Using eval with user input
            const result = eval(expression);
            return result;
        } catch (error) {
            return Error: ;
        }
    }
}

// Test the vulnerabilities
const vulnerableApp = new VulnerableApp();

console.log("Testing vulnerable Node.js application:");

// Test SQL injection
const sqlResult = vulnerableApp.authenticateUser("admin' OR '1'='1", "any");
console.log("SQL injection test result:", sqlResult);

// Test weak hashing
const weakHash = vulnerableApp.hashPassword("password123");
console.log("Weak MD5 hash:", weakHash);

// Test command injection
vulnerableApp.executeSystemCommand("test && echo vulnerable", (err, output) => {
    if (!err) console.log("Command injection result:", output);
});

module.exports = VulnerableApp;
