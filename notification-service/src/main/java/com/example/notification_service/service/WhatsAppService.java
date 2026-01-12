package com.example.notification_service.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class WhatsAppService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp-number}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null) accountSid = accountSid.trim();
        if (authToken != null) authToken = authToken.trim();
        if (fromNumber != null) fromNumber = fromNumber.trim();

        boolean configured = true;
        if (accountSid == null || authToken == null) configured = false;
        if ("TWILIO_NOT_CONFIGURED".equals(accountSid) || "TWILIO_NOT_CONFIGURED".equals(authToken)) configured = false;

        if (!configured) {
            log.warn("Twilio not configured. Set TWILIO_ACCOUNT_SID and TWILIO_AUTH_TOKEN environment variables (or properties) to enable WhatsApp sends.");
            return;
        }

        try {
            Twilio.init(accountSid, authToken);
            String maskedSid = accountSid.length() > 8 ? accountSid.substring(0,4) + "..." + accountSid.substring(accountSid.length()-4) : "****";
            log.info("Twilio initialized (accountSid={}) using fromNumber={}", maskedSid, fromNumber);
        } catch (Exception e) {
            log.error("Twilio initialization failed: {}", e.toString(), e);
        }
    }

    public void sendWelcome(String toPhone, String name) {
        String msg = String.format(
                "Welcome to Hiru Sandu, %s! 🎉\n\n" +
                        "Thank you for choosing us for your special day.",
                name
        );
        sendMessage(toPhone, msg);
    }

    public void sendPaymentConfirmation(String toPhone, Map<String, String> data) {
        try {
            if (data == null) data = java.util.Collections.emptyMap();
            String orderId = data.getOrDefault("orderId", data.getOrDefault("billingCode", "-"));
            String amount = data.getOrDefault("amount", "-");
            String paymentMethod = data.getOrDefault("paymentMethod", "-");
            String items = data.getOrDefault("items", "");

            StringBuilder sb = new StringBuilder();
            sb.append("Payment Confirmed! ✅\n\n");
            sb.append("Order ID: ").append(orderId).append("\n");
            if (paymentMethod != null && !paymentMethod.isBlank()) {
                sb.append("Payment Method: ").append(paymentMethod).append("\n");
            }
            if (items != null && !items.isBlank()) {
                sb.append("Items: ").append("\n").append(items).append("\n");
            }
            sb.append("Net Payment: Rs. ").append(amount).append("\n");
            sb.append("\nThank you!");

            log.info("WhatsApp payment payload for {} -> {}", toPhone, data);

            sendMessage(toPhone, sb.toString());
        } catch (Exception e) {
            log.error("Failed to build WhatsApp message: {}", e.getMessage());
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    private void sendMessage(String toPhone, String text) {
        if ("TWILIO_NOT_CONFIGURED".equals(accountSid) || "TWILIO_NOT_CONFIGURED".equals(authToken) || accountSid == null || authToken == null) {
            log.warn("Twilio not configured - skipping actual send. To enable, set TWILIO_ACCOUNT_SID and TWILIO_AUTH_TOKEN.");
            log.info("WhatsApp (simulated) to: {} | message: {}", toPhone, text);
            return;
        }

        try {
            String normalized = toPhone == null ? "" : toPhone.trim();
            normalized = normalized.replaceAll("[^+0-9]", "");

            if (normalized.startsWith("0")) {
                normalized = "+94" + normalized.substring(1);
            } else if (normalized.startsWith("94")) {
                normalized = "+" + normalized;
            } else if (!normalized.startsWith("+")) {
                if (normalized.length() == 9) {
                    normalized = "+94" + normalized;
                } else {
                    normalized = "+" + normalized;
                }
            }

            String to = "whatsapp:" + normalized;
            String from = fromNumber.startsWith("whatsapp:") ? fromNumber : "whatsapp:" + fromNumber;

            log.info("=== WhatsApp Send ===");
            log.info("To: {}", to);
            log.info("From: {}", from);
            log.info("Message: {}", text);

            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(from),
                    text
            ).create();

            log.info("SUCCESS - SID: {}, Status: {}", message.getSid(), message.getStatus());

        } catch (Exception e) {
            log.error("FAILED - {}", e.getMessage());
            log.error("Full error:", e);
            throw new RuntimeException("WhatsApp failed: " + e.getMessage(), e);
        }
    }
}