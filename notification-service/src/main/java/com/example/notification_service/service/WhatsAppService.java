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
        StringBuilder sb = new StringBuilder();

        // Header

        sb.append("    ✨ *WELCOME* ✨\n");

        sb.append("*HIRU SANDU BRIDAL WEARE*\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        // Personalized greeting
        sb.append("Dear *").append(name).append("*, 👋\n\n");

        sb.append("Welcome to Hiru Sandu Bridal Weare! We are honored that you have chosen us to be part of your special celebration. 💍\n\n");

        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("🌟 *OUR COMMITMENT TO YOU*\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("✓ Premium Quality Bridal Wear\n");
        sb.append("✓ Personalized Customer Service\n");
        sb.append("✓ Timely Delivery & Support\n");
        sb.append("✓ Making Your Day Memorable\n\n");

        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("💬 *Need Assistance?*\n");
        sb.append("Our team is here to help you with any questions or special requests.\n\n");

        sb.append("Thank you for trusting us with your special moments! 🎉\n\n");

        sb.append("_Warm Regards,_\n");
        sb.append("_Hiru Sandu Bridal Weare Team_ 💐\n");

        sendMessage(toPhone, sb.toString());
    }

    public void sendPaymentConfirmation(String toPhone, Map<String, String> data) {
        try {
            if (data == null) data = java.util.Collections.emptyMap();

            // Extract billing details
            String billNumber = data.getOrDefault("billNumber", data.getOrDefault("orderId", data.getOrDefault("billingCode", "-")));
            String paymentMethod = data.getOrDefault("paymentMethod", "Cash");
            String items = data.getOrDefault("items", "");
            String subtotal = data.getOrDefault("subtotal", data.getOrDefault("amount", "0.00"));
            String discount = data.getOrDefault("discount", "0");
            String discountPercent = data.getOrDefault("discountPercent", "0");
            String netAmount = data.getOrDefault("netAmount", data.getOrDefault("amount", "0.00"));
            String customerName = data.getOrDefault("customerName", "Valued Customer");

            StringBuilder sb = new StringBuilder();

            // Header

            sb.append("    💳 *PAYMENT RECEIPT* 💳\n");

            sb.append("*HIRU SANDU BRIDAL WEARE*\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

            // Bill Information
            sb.append("📋 *Bill No:* ").append(billNumber).append("\n");
            sb.append("👤 *Customer:* ").append(customerName).append("\n");
            sb.append("💰 *Payment Method:* ").append(paymentMethod).append("\n");
            sb.append("📅 *Date:* ").append(data.getOrDefault("date", java.time.LocalDate.now().toString())).append("\n\n");

            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("📦 *ITEMS ORDERED*\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

            // Items Table
            if (items != null && !items.isBlank()) {
                sb.append(items).append("\n");
            } else {
                sb.append("No items listed\n\n");
            }

            // Billing Summary
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("💵 *BILLING SUMMARY*\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

            sb.append("Subtotal:        Rs. ").append(String.format("%10s", subtotal)).append("\n");

            if (!discount.equals("0") && !discount.isEmpty()) {
                sb.append("Discount (").append(discountPercent).append("%):   Rs. ").append(String.format("%10s", discount)).append("\n");
                sb.append("                 ").append("─────────────\n");
            }

            sb.append("*NET AMOUNT:     Rs. ").append(String.format("%10s", netAmount)).append("*\n\n");

            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

            // Footer
            sb.append("✅ *Payment Confirmed Successfully!*\n\n");
            sb.append("Thank you for choosing us for your special day! 🎉\n\n");
            sb.append("For any queries, feel free to contact us.\n\n");
            sb.append("_Best Regards,_\n");
            sb.append("_Hiru Sandu Bridal Weare Team_ 💐\n");

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