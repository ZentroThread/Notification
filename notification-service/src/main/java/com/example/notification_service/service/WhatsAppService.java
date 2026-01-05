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
        Twilio.init(accountSid, authToken);
        log.info("Twilio initialized");
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
        try {
            if (!toPhone.startsWith("+")) {
                toPhone = "+94" + toPhone;
            }

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + toPhone),
                    new PhoneNumber(fromNumber),
                    text
            ).create();

            log.info("WhatsApp sent: {}", message.getSid());
        } catch (Exception e) {
            log.error("WhatsApp failed: {}", e.getMessage());
            throw new RuntimeException("WhatsApp failed", e);
        }
    }
}