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

    SimpleMailMessage message = new SimpleMailMessage();

    message.setFrom("jobportalsystemapplication@gmail.com"); // ADD THIS
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

    mailSender.send(message);
}
}
