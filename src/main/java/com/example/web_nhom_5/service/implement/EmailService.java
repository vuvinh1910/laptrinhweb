package com.example.web_nhom_5.service.implement;

import com.example.web_nhom_5.dto.EmailBody;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(EmailBody emailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailBody.to());
        message.setFrom("levuthanhvinhk39a@gmail.com");
        message.setSubject(emailBody.subject());
        message.setText(emailBody.text());
        javaMailSender.send(message);
    }
}
