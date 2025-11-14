package com.megachat.config;

import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Component
public class DatabaseInitializer {

    @EventListener(ApplicationContextInitializedEvent.class)
    public void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to MySQL server without specifying a database
            // Lưu ý: Cập nhật password theo cấu hình MySQL của bạn
            String url = "jdbc:mysql://localhost:3307/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String user = "root";
            String password = ""; // Để trống nếu MySQL không có password, hoặc nhập password của bạn
            
            try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {
                
                // Create database if not exists
                stmt.execute("CREATE DATABASE IF NOT EXISTS megachat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                System.out.println("✓ Database 'megachat' created or already exists");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
