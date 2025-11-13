package com.megachat.controller;

import com.megachat.model.User;
import com.megachat.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String email = request.get("email");
            String phone = request.get("phone");
            String password = request.get("password");
            String confirmPassword = request.get("confirm_password");

            // Validate input
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Username không được để trống"
                ));
            }

            if (!password.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Mật khẩu không trùng khớp"
                ));
            }

            // Register user
            User user = userService.register(username, email, phone, password);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng ký thành công");
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "phone", user.getPhone()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String emailOrPhone = request.get("email");
            String password = request.get("password");

            if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email hoặc số điện thoại không được để trống"
                ));
            }

            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Mật khẩu không được để trống"
                ));
            }

            // Login user
            User user = userService.login(emailOrPhone, password);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng nhập thành công");
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "phone", user.getPhone()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new Exception("Người dùng không tồn tại"));

            return ResponseEntity.ok(Map.of(
                "success", true,
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "phone", user.getPhone(),
                    "created_at", user.getCreatedAt()
                )
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
