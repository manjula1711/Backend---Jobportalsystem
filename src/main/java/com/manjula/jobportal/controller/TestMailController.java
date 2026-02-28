package com.manjula.jobportal.controller;

import com.manjula.jobportal.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestMailController {

    @Autowired
    private EmailService emailService;

    // ✅ Simple test endpoint
    // Example:
    // https://your-backend-url/api/test/send-mail?to=yourgmail@gmail.com
    @GetMapping("/send-mail")
    public String sendTestMail(@RequestParam String to) {

        try {
            String testLink = "https://example.com/reset?token=123456";

            emailService.sendResetPasswordEmail(to, testLink);

            return "✅ Test email sent successfully to: " + to;
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send email: " + e.getMessage();
        }
    }
}
