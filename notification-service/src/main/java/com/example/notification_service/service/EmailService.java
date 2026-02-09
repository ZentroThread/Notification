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
        String subject = "Welcome to Hiru Sandu Bridal Wears — Next steps to get started";
        String profileUrl = "https://hirusandu.com/"; // TODO: replace with real profile URL
        String catalogUrl = "https://hirusandu.com/#featured-products"; // TODO: replace with real catalog URL
        String bookingUrl = "https://hirusandu.com//contact.php"; // TODO: replace with real booking URL

        String htmlBody = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #222; margin: 0; padding: 0; background: #f5f7f8; }
                    .container { max-width: 640px; margin: 24px auto; padding: 16px; }
                    .card { background: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 18px rgba(0,0,0,0.06); }
                    .header { background: linear-gradient(90deg,#11998e 0%%,#38ef7d 100%%); color: #fff; padding: 28px 20px; text-align: center; }
                    .header h1 { margin: 0; font-size: 20px; }
                    .body { padding: 22px; color: #333; line-height: 1.5; }
                    .lead { font-size: 16px; margin-bottom: 12px; }
                    .actions { display:flex; gap:12px; flex-wrap:wrap; margin:18px 0; }
                    .btn { background: #11998e; color: #fff; padding: 12px 16px; border-radius: 6px; text-decoration: none; font-weight:600; display:inline-block; }
                    .secondary { background: #f1f7f5; color: #11998e; border: 1px solid #dcefe6; }
                    .note { font-size:13px; color:#666; margin-top:14px; }
                    .footer { background:#fafafa; padding:14px; text-align:center; font-size:13px; color:#888; }
                    @media (max-width:480px) { .actions { flex-direction:column; } }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="card">
                      <div class="header">
                        <h1>Welcome, %s</h1>
                        <div style="opacity:0.95; margin-top:6px;">Thank you for choosing Hiru Sandu Bridal Wears</div>
                      </div>
                      <div class="body">
                        <p class="lead">We're delighted to help you prepare for your special day. Below are a few quick actions to get you started.</p>

                        <div class="actions">
                          <a class="btn" href="%s">View Curated Collections</a>
                          <a class="btn secondary" href="%s">Complete Your Profile</a>
                          <a class="btn" href="%s">Book a Consultation</a>
                        </div>

                        <p>If you'd like personalized recommendations, reply to this email with your preferred styles or measurements and our team will follow up to assist you.</p>

                        <p class="note">Need immediate help? Contact our support team at <a href="mailto:support@hirusandu.example">support@hirusandu.example</a> or call +94 11 123 4567.</p>

                        <p style="margin-bottom:0;">Warm regards,<br><strong>Hiru Sandu Bridal Wears</strong></p>
                      </div>
                      <div class="footer">
                        Manage preferences | <a href="#">Unsubscribe</a>
                      </div>
                    </div>
                  </div>
                </body>
                </html>
                """,
                name, catalogUrl, profileUrl, bookingUrl
        );

        String plainText = String.format("Welcome %s!\n\nThank you for choosing Hiru Sandu Bridal Wears.\n\nQuick actions:\n- View Curated Collections: %s\n- Complete Your Profile: %s\n- Book a Consultation: %s\n\nReply to this email for personalized assistance or contact support@hirusandu.example.", name, catalogUrl, profileUrl, bookingUrl);

        sendHtmlEmail(toEmail, subject, plainText, htmlBody);
    }

    public void sendPaymentConfirmation(String toEmail, Map<String, String> data) {
        String subject = "Payment Confirmation - Order #" + data.get("orderId");
        String htmlBody = String.format("""
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

        String plainText = String.format("Payment confirmed for Order #%s\nPayment method: %s\nAmount: Rs. %s\nDate: %s\n\nThank you for choosing Hiru Sandu Bridal Wears.",
                data.get("orderId"), data.get("paymentMethod"), data.get("amount"), java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")));

        sendHtmlEmail(toEmail, subject, plainText, htmlBody);
    }

    private void sendHtmlEmail(String to, String subject, String plainText, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            // provide plain-text and HTML alternatives
            helper.setText(plainText, html);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Email failed", e);
        }
    }
}