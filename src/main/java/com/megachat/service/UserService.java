package com.megachat.service;

import com.megachat.model.User;
import com.megachat.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String email, String phone, String password) throws Exception {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username không được để trống");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email không được để trống");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new Exception("Số điện thoại không được để trống");
        }
        if (password == null || password.length() < 6) {
            throw new Exception("Mật khẩu phải có ít nhất 6 ký tự");
        }

        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new Exception("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(email)) {
            throw new Exception("Email đã tồn tại");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new Exception("Số điện thoại đã tồn tại");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(encodePassword(password)); // In production, use BCrypt

        return userRepository.save(user);
    }

    public User login(String emailOrPhone, String password) throws Exception {
        // Try to find by email first
        Optional<User> userOpt = userRepository.findByEmail(emailOrPhone);
        
        // If not found, try by phone
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByPhone(emailOrPhone);
        }

        if (userOpt.isEmpty()) {
            throw new Exception("Email hoặc số điện thoại không tồn tại");
        }

        User user = userOpt.get();
        if (!verifyPassword(password, user.getPassword())) {
            throw new Exception("Mật khẩu không chính xác");
        }

        return user;
    }

    // Simple password encoding (in production use BCryptPasswordEncoder)
    private String encodePassword(String password) {
        // For now, just return as is. In production, use BCrypt or similar
        return password;
    }

    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        // For now, direct comparison. In production, use BCrypt verification
        return rawPassword.equals(encodedPassword);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
