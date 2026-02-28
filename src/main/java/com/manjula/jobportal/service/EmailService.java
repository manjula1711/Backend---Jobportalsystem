package com.manjula.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendResetPasswordEmail(String toEmail, String resetLink) {

        System.out.println("📧 Sending mail to: " + toEmail);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);   // VERY IMPORTANT
        message.setTo(toEmail);
        message.setSubject("Job Portal - Password Reset Request");

        message.setText(
                "Hi,\n\n" +
                "Click below to reset your password:\n\n" +
                resetLink + "\n\n" +
                "This link expires in 15 minutes.\n\n" +
                "Job Portal Team"
        );

        mailSender.send(message);

        System.out.println("✅ Mail sent successfully!");
    }
}
