package com.manjula.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String resetLink) {

        System.out.println("✅ EmailService called. TO = " + toEmail);
        System.out.println("✅ ResetLink = " + resetLink);

        SimpleMailMessage message = new SimpleMailMessage();

        // ✅ For now keep it WITHOUT setFrom (Brevo will use default sender)
        // After sender verification in Brevo, you can enable this:
        // message.setFrom("jobportalsystemapplication@gmail.com");

        message.setTo(toEmail);
        message.setSubject("Job Portal - Password Reset Request");
        message.setText(
                "Hi,\n\n" +
                "We received a request to reset your password.\n\n" +
                "Click the link below to reset your password:\n" +
                resetLink + "\n\n" +
                "This link will expire in 15 minutes.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Thanks,\nJob Portal Team"
        );

        try {
            mailSender.send(message);
            System.out.println("✅ Mail sent successfully via SMTP");
        } catch (Exception e) {
            System.out.println("❌ SMTP mail failed: " + e.getMessage());
            e.printStackTrace();
            throw e; // ✅ important: so API will fail instead of showing fake 200 OK
        }
    }
}
