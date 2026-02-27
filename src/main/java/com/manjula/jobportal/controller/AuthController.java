package com.manjula.jobportal.controller;

import com.manjula.jobportal.dto.ForgotPasswordRequest;
import com.manjula.jobportal.dto.LoginRequest;
import com.manjula.jobportal.dto.RegisterRequest;
import com.manjula.jobportal.dto.ResetPasswordRequest;
import com.manjula.jobportal.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String msg = authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(java.util.Map.of("message", msg));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        String msg = authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(java.util.Map.of("message", msg));
    }

    // Optional: simple backend page (so you can test link even without frontend)
    @GetMapping("/reset-password-page")
    public ResponseEntity<String> resetPasswordPage(@RequestParam String token) {
        String html = """
            <html>
              <body style="font-family:Arial">
                <h2>Reset Password (Backend Test Page)</h2>
                <p>Token received:</p>
                <code>%s</code>
                <p>Now call POST /api/auth/reset-password with JSON:</p>
                <pre>{
  "token": "%s",
  "newPassword": "NewPassword@123"
}</pre>
              </body>
            </html>
        """.formatted(token, token);

        return ResponseEntity.ok(html);
    }
}
