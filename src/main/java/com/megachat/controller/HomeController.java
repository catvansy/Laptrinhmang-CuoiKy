package com.megachat.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Home Controller - Xử lý root path và serve static files
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public void home(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + "/index.html");
    }

    @GetMapping("/index.html")
    public ResponseEntity<Resource> index() throws IOException {
        return serveHtmlFile("static/index.html");
    }

    @GetMapping("/landing.html")
    public ResponseEntity<Resource> landing() throws IOException {
        return serveHtmlFile("static/landing.html");
    }

    @GetMapping("/login.html")
    public ResponseEntity<Resource> login() throws IOException {
        return serveHtmlFile("static/login.html");
    }

    @GetMapping("/chat.html")
    public ResponseEntity<Resource> chat() throws IOException {
        return serveHtmlFile("static/chat.html");
    }

    @GetMapping("/forgot-password.html")
    public ResponseEntity<Resource> forgotPassword() throws IOException {
        return serveHtmlFile("static/forgot-password.html");
    }

    private ResponseEntity<Resource> serveHtmlFile(String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header(HttpHeaders.CONTENT_TYPE, "text/html;charset=UTF-8")
                .body(resource);
    }
}

