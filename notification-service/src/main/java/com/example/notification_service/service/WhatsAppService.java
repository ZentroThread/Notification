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
        if ("TWILIO_NOT_CONFIGURED".equals(accountSid) || "TWILIO_NOT_CONFIGURED".equals(authToken)) {
            return;
        }
        try {
            Twilio.init(accountSid, authToken);
        } catch (Exception e) {
            log.error("Twilio initialization failed: {}", e.getMessage());
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
        String msg = String.format(
                "Payment Confirmed! ✅\n\n" +
                        "Order ID: %s\n" +
                        "Amount: Rs. %s\n\n" +
                        "Thank you!",
                data.get("orderId"),
                data.get("amount")
        );
        sendMessage(toPhone, msg);
    }

    private void sendMessage(String toPhone, String text) {
        if ("TWILIO_NOT_CONFIGURED".equals(accountSid) || "TWILIO_NOT_CONFIGURED".equals(authToken)) {
            throw new RuntimeException("Twilio not configured");
        }

        try {
            String normalized = toPhone == null ? "" : toPhone.trim();

            // Normalize local Sri Lankan numbers starting with 0 (e.g., 077xxxxxxx -> +9477xxxxxxx)
            if (normalized.startsWith("0")) {
                normalized = "+94" + normalized.substring(1);
            } else if (!normalized.startsWith("+")) {
                // If number provided without + but with country code (e.g., 9477...), add +
                normalized = "+" + normalized;
            }

            log.info("Sending WhatsApp to: whatsapp:{}", normalized);

            Message.creator(
                    new PhoneNumber("whatsapp:" + normalized),
                    new PhoneNumber(fromNumber),
                    text
            ).create();
        } catch (Exception e) {
            log.error("WhatsApp send failed for {}: {}", toPhone, e.getMessage());
            throw new RuntimeException("WhatsApp failed", e);
        }
    }
}