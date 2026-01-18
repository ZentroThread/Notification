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
        String body = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; }
                        .header { background: linear-gradient(135deg, #11998e 0%%, #38ef7d 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background-color: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                        .success-icon { font-size: 48px; margin-bottom: 10px; }
                        .title { margin: 0; font-size: 28px; font-weight: bold; }
                        .subtitle { margin: 10px 0 0 0; font-size: 16px; opacity: 0.9; }
                        .message { font-size: 16px; margin-bottom: 25px; }
                        .details-box { background-color: #f8f9fa; border: 1px solid #e9ecef; border-radius: 8px; padding: 20px; margin: 20px 0; }
                        .detail-row { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #e9ecef; }
                        .detail-row:last-child { border-bottom: none; }
                        .detail-label { font-weight: 600; color: #666; }
                        .detail-value { font-weight: 700; color: #11998e; text-align: right; }
                        .amount-highlight { font-size: 24px; color: #11998e; }
                        .info-box { background-color: #e8f5e9; border-left: 4px solid #4caf50; padding: 15px; margin: 20px 0; border-radius: 4px; }
                        .footer { text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; color: #666; font-size: 14px; }
                        .help-text { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 4px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <div class="success-icon">✓</div>
                            <h1 class="title">Payment Successful!</h1>
                            <p class="subtitle">Thank you for your payment</p>
                        </div>
                        <div class="content">
                            <p class="message">
                                Dear Valued Customer,
                            </p>
                            
                            <p class="message">
                                We are pleased to confirm that your payment has been successfully processed. 
                                Below are the details of your transaction:
                            </p>
                            
                            <div class="details-box">
                                <div class="detail-row">
                                    <span class="detail-label">Order ID:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Payment Method:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Amount Paid:</span>
                                    <span class="detail-value amount-highlight">Rs. %s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Transaction Date:</span>
                                    <span class="detail-value">%s</span>
                                </div>
                            </div>
                            
                            <div class="info-box">
                                <strong>✓ Payment Confirmed</strong><br>
                                Your payment has been successfully processed and your order is now being prepared. 
                                We will notify you once your order is ready for pickup/delivery.
                            </div>
                            
                            <div class="help-text">
                                <strong>📞 Need Help?</strong><br>
                                If you have any questions about your order or payment, please don't hesitate to contact our customer support team.
                            </div>
                            
                            <p class="message">
                                Thank you for choosing <strong>Hiru Sandu Bridal Wears</strong>. 
                                We look forward to serving you!
                            </p>
                            
                            <div class="footer">
                                <p><strong>Hiru Sandu Bridal Wears</strong></p>
                                <p>Making Your Special Moments Unforgettable</p>
                                <p style="font-size: 12px; color: #999; margin-top: 15px;">
                                    This is an automated confirmation email. Please keep this for your records.<br>
                                    For inquiries, please contact our support team.
                                </p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """,
                data.get("orderId"),
                data.get("paymentMethod"),
                data.get("amount"),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))
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
        } catch (MessagingException e) {
            throw new RuntimeException("Email failed", e);
        }
    }
}