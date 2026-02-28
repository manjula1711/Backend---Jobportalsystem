package com.manjula.jobportal.controller;

import com.manjula.jobportal.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestMailController {

    private final EmailService emailService;

    public TestMailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-mail")
    public String sendMail(@RequestParam String to) {
        emailService.sendResetPasswordEmail(to, "https://example.com/reset?token=123");
        return "TEST MAIL TRIGGERED";
    }
}
