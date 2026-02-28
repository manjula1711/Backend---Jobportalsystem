package com.manjula.jobportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class BrevoEmailService {

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    private final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendResetPasswordEmail(String toEmail, String resetLink) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> body = new HashMap<>();

        // Sender
        Map<String, String> sender = new HashMap<>();
        sender.put("name", "Job Portal");
        sender.put("email", "jobportalsystemapplication@gmail.com");

        // Receiver
        Map<String, String> to = new HashMap<>();
        to.put("email", toEmail);

        body.put("sender", sender);
        body.put("to", new Map[]{to});
        body.put("subject", "Reset your password");

        String htmlContent =
                "<h2>Password Reset</h2>" +
                "<p>Click the link below to reset your password:</p>" +
                "<a href='" + resetLink + "'>Reset Password</a>" +
                "<p>This link expires in 15 minutes.</p>";

        body.put("htmlContent", htmlContent);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(BREVO_URL, request, String.class);

        System.out.println("Brevo Response: " + response.getBody());
    }
}
