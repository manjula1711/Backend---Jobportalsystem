package com.manjula.jobportal.service;

import com.manjula.jobportal.config.JwtUtil;
import com.manjula.jobportal.dto.LoginRequest;
import com.manjula.jobportal.dto.RegisterRequest;
import com.manjula.jobportal.model.PasswordResetToken;
import com.manjula.jobportal.model.Role;
import com.manjula.jobportal.model.User;
import com.manjula.jobportal.repository.PasswordResetTokenRepository;
import com.manjula.jobportal.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService; // ✅ keep this (or BrevoEmailService if you renamed)

    @Value("${app.frontend.reset-url}")
    private String resetUrl;

    @PostConstruct
    public void createDefaultAdmin() {
        Optional<User> adminOptional = userRepository.findByEmail("admin@gmail.com");
        if (adminOptional.isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Default Admin Created Successfully");
        }
    }

    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "❌ Email already exists!";
        }
        if (request.getRole() == Role.ADMIN) {
            return "❌ Cannot register as ADMIN!";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        userRepository.save(user);

        return "✅ User registered successfully!";
    }

    public Map<String, String> login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("❌ Invalid Email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("❌ Invalid Password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().name());
        response.put("name", user.getName());
        return response;
    }

    /**
     * ✅ Forgot Password
     * - Always return success message (security)
     * - Only send mail if user exists
     */
    @Transactional
    public String forgotPassword(String email) {

        final String successMsg = "If the email exists, a reset link has been sent.";

        Optional<User> userOpt = userRepository.findByEmail(email);

        // ✅ Don’t reveal whether email exists
        if (userOpt.isEmpty()) {
            return successMsg;
        }

        User user = userOpt.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByUser(user)
                .orElseGet(PasswordResetToken::new);

        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(expiry);

        passwordResetTokenRepository.save(resetToken);

        String link = resetUrl + "?token=" + token;

        System.out.println("===== PASSWORD RESET EMAIL =====");
        System.out.println("User: " + user.getEmail());
        System.out.println("Token: " + token);
        System.out.println("Link: " + link);

        try {
            emailService.sendResetPasswordEmail(user.getEmail(), link);
        } catch (Exception e) {
            System.out.println("❌ Forgot Password email failed: " + e.getMessage());
            e.printStackTrace();
            // still return success (avoid frontend 403)
            return successMsg;
        }

        return successMsg;
    }

    @Transactional
    public String resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        return "Password updated successfully";
    }
}
