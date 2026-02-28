package com.manjula.jobportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class BrevoEmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendResetPasswordEmail(String toEmail, String resetLink) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ IMPORTANT: Brevo expects this header name
        headers.set("api-key", brevoApiKey);

        Map<String, Object> body = new HashMap<>();

        // ✅ sender must be verified in Brevo (your screenshot shows verified)
        Map<String, String> sender = new HashMap<>();
        sender.put("name", "Job Portal");
        sender.put("email", "jobportalsystemapplication@gmail.com");
        body.put("sender", sender);

        Map<String, String> to = new HashMap<>();
        to.put("email", toEmail);
        body.put("to", new Object[]{to});

        body.put("subject", "Job Portal - Password Reset Request");

        String text =
                "Hi,\n\n" +
                "We received a request to reset your password.\n\n" +
                "Click the link below to reset your password:\n" +
                resetLink + "\n\n" +
                "This link will expire in 15 minutes.\n\n" +
                "Thanks,\nJob Portal Team";

        body.put("textContent", text);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Brevo failed: " + response.getStatusCode() + " -> " + response.getBody());
        }
    }
}
