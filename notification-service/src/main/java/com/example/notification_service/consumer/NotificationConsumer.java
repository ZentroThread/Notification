package com.example.notification_service.consumer;

import com.example.notification_service.model.NotificationEvent;
import com.example.notification_service.service.EmailService;
import com.example.notification_service.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final WhatsAppService whatsAppService;
    private final EmailService emailService;

    @KafkaListener(
            topics = "notifications",
            groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(NotificationEvent event) {

        if (event == null) {
            log.error("Received null event");
            return;
        }

        try {
            switch (event.getEventType()) {
                case "WELCOME" -> sendWelcome(event);
                case "PAYMENT_CONFIRMED" -> sendPayment(event);
                default -> log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Notification failed", e);
        }
    }

    // Added helper methods to dispatch notifications based on priority and available recipients.
    private void sendWelcome(NotificationEvent event) {
        if (event == null) return;

        Integer priority = event.getPriority();
        String phone = event.getRecipientPhone();
        String email = event.getRecipientEmail();
        String name = event.getRecipientName();

        // priority: 1 = WhatsApp preferred, 2 = Email preferred. If null, try WhatsApp then Email.
        if (priority != null && priority == 2) {
            // Email preferred
            if (email != null && !email.isBlank()) {
                try {
                    emailService.sendWelcome(email, name);
                } catch (Exception e) {
                    log.error("Failed to send welcome email to {}: {}", email, e.getMessage());
                }
            } else {
                log.warn("No recipient email available for welcome event {}", event.getEventId());
            }

            // send WhatsApp as secondary if phone available
            if (phone != null && !phone.isBlank()) {
                try {
                    whatsAppService.sendWelcome(phone, name);
                } catch (Exception e) {
                    log.error("Failed to send welcome WhatsApp to {}: {}", phone, e.getMessage());
                }
            }
        } else {
            // WhatsApp preferred (priority == 1 or null)
            if (phone != null && !phone.isBlank()) {
                try {
                    whatsAppService.sendWelcome(phone, name);
                } catch (Exception e) {
                    log.error("Failed to send welcome WhatsApp to {}: {}", phone, e.getMessage());
                }
            } else {
                log.warn("No recipient phone available for welcome event {}", event.getEventId());
            }

            // send Email as secondary if email available
            if (email != null && !email.isBlank()) {
                try {
                    emailService.sendWelcome(email, name);
                } catch (Exception e) {
                    log.error("Failed to send welcome email to {}: {}", email, e.getMessage());
                }
            }
        }
    }

    private void sendPayment(NotificationEvent event) {
        if (event == null) return;

        Integer priority = event.getPriority();
        String phone = event.getRecipientPhone();
        String email = event.getRecipientEmail();
        java.util.Map<String, String> data = event.getTemplateData();

        if (data == null) data = java.util.Collections.emptyMap();

        // priority: 1 = WhatsApp preferred, 2 = Email preferred. If null, try WhatsApp then Email.
        if (priority != null && priority == 2) {
            // Email preferred
            if (email != null && !email.isBlank()) {
                try {
                    emailService.sendPaymentConfirmation(email, data);
                } catch (Exception e) {
                    log.error("Failed to send payment confirmation email to {}: {}", email, e.getMessage());
                }
            } else {
                log.warn("No recipient email available for payment event {}", event.getEventId());
            }

            if (phone != null && !phone.isBlank()) {
                try {
                    whatsAppService.sendPaymentConfirmation(phone, data);
                } catch (Exception e) {
                    log.error("Failed to send payment confirmation WhatsApp to {}: {}", phone, e.getMessage());
                }
            }
        } else {
            // WhatsApp preferred (priority == 1 or null)
            if (phone != null && !phone.isBlank()) {
                try {
                    whatsAppService.sendPaymentConfirmation(phone, data);
                } catch (Exception e) {
                    log.error("Failed to send payment confirmation WhatsApp to {}: {}", phone, e.getMessage());
                }
            } else {
                log.warn("No recipient phone available for payment event {}", event.getEventId());
            }

            if (email != null && !email.isBlank()) {
                try {
                    emailService.sendPaymentConfirmation(email, data);
                } catch (Exception e) {
                    log.error("Failed to send payment confirmation email to {}: {}", email, e.getMessage());
                }
            }
        }
    }

}
