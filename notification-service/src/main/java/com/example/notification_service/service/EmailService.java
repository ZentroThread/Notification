package com.example.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendWelcome(String toEmail, String name) {
        String subject = "Welcome to Hiru Sandu! 🎉";
        String body = String.format(
                "<h2>Welcome %s!</h2>" +
                        "<p>Thank you for choosing Hiru Sandu Bridal Wears.</p>" +
                        "<p>We're excited to help you with your special day!</p>",
                name
        );
        sendHtmlEmail(toEmail, subject, body);
    }

    public void sendPaymentConfirmation(String toEmail, Map<String, String> data) {
        String subject = "Payment Confirmation - Order #" + data.get("orderId");
        String body = String.format(
                "<h2>Payment Confirmed!</h2>" +
                        "<p>Order ID: %s</p>" +
                        "<p>Amount: Rs. %s</p>" +
                        "<p>Payment Method: %s</p>",
                data.get("orderId"),
                data.get("amount"),
                data.get("paymentMethod")
        );
        sendHtmlEmail(toEmail, subject, body);
    }

    private void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Email failed: {}", e.getMessage());
            throw new RuntimeException("Email failed", e);
        }
    }
}