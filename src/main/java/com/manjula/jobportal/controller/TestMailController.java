package com.manjula.jobportal.controller;

import com.manjula.jobportal.service.BrevoEmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestMailController {

    private final BrevoEmailService brevoEmailService;

    public TestMailController(BrevoEmailService brevoEmailService) {
        this.brevoEmailService = brevoEmailService;
    }

    // Example:
    // https://jobportalsystem-4fu4.onrender.com/api/test/send-mail?to=manjula171103@gmail.com
    @GetMapping("/send-mail")
    public String test(@RequestParam String to) {
        brevoEmailService.sendResetPasswordEmail(to, "https://example.com/reset?token=123");
        return "Mail triggered via Brevo API ✅";
    }
}
