package com.example;

import java.sql.*;
import java.security.MessageDigest;
import java.util.Scanner;
import java.io.*;

/**
 * Deliberately vulnerable Java application for SAST testing
 * Contains multiple security vulnerabilities for demonstration
 */
public class VulnerableApp {
    
    // CWE-798: Hardcoded credentials
    private static final String DB_PASSWORD = "admin123!";
    private static final String API_KEY = "sk-1234567890abcdef";
    private static final String SECRET_TOKEN = "ghp_1234567890abcdef1234567890abcdef12";
    
    // Database connection details  
    private static final String DB_URL = "jdbc:mysql://localhost:3306/app";
    private static final String DB_USER = "root";
    
    /**
     * CWE-89: SQL Injection vulnerability
     * Directly concatenates user input into SQL query
     */
    public User getUserByUsername(String username) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        User user = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            
            // VULNERABLE: Direct string concatenation
            String sql = "SELECT * FROM users WHERE username = '" + username + "'";
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return user;
    }
    
    /**
     * CWE-89: Another SQL injection with dynamic query building
     */
    public boolean authenticateUser(String email, String password) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        
        // VULNERABLE: User input directly in query
        String query = "SELECT COUNT(*) FROM users WHERE email = '" + email + 
                      "' AND password = '" + password + "'";
        
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        return rs.getInt(1) > 0;
    }
    
    /**
     * CWE-327: Weak cryptographic algorithm
     */
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // VULNERABLE: MD5 is weak
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (Exception e) {
            return password; // VULNERABLE: Returning plaintext on error
        }
    }
    
    /**
     * CWE-22: Path traversal vulnerability
     */
    public String readFile(String filename) {
        try {
            // VULNERABLE: No path validation
            File file = new File("/app/files/" + filename);
            Scanner scanner = new Scanner(file);
            StringBuilder content = new StringBuilder();
            
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
            
            scanner.close();
            return content.toString();
        } catch (FileNotFoundException e) {
            return "File not found";
        }
    }
    
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    
    public static void main(String[] args) {
        VulnerableApp app = new VulnerableApp();
        
        // Test SQL injection
        User user = app.getUserByUsername("admin' OR '1'='1");
        System.out.println("Retrieved user: " + (user != null ? user.getUsername() : "null"));
        
        // Test weak hashing
        String hashedPassword = app.hashPassword("password123");
        System.out.println("Hashed password: " + hashedPassword);
    }
}

class User {
    private int id;
    private String username;
    private String password;
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
