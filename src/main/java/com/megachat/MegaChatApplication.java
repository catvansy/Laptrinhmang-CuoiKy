package com.megachat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MegaChat Application - Spring Boot Main Class
 * Tomcat embedded tự động chạy trên port 8080
 */
@SpringBootApplication
public class MegaChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MegaChatApplication.class, args);
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  🎯 MegaChat Web - Spring Boot Started║");
        System.out.println("║  http://localhost:8080/megachat       ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }
}
