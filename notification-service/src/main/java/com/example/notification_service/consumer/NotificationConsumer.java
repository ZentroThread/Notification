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

    @KafkaListener(topics = "notifications", groupId = "notification-service-group")
    public void consume(NotificationEvent event) {
        log.info("Received: {} for {}", event.getEventType(), event.getRecipientName());

        try {
            switch (event.getEventType()) {
                case "WELCOME":
                    sendWelcome(event);
                    break;
                case "PAYMENT_CONFIRMED":
                    sendPayment(event);
                    break;
                default:
                    log.warn("Unknown type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed: {}", e.getMessage());
        }
    }

    private void sendWelcome(NotificationEvent event) {
        if (event.getPriority() == 1) {
            try {
                whatsAppService.sendWelcome(
                        event.getRecipientPhone(),
                        event.getRecipientName()
                );
            } catch (Exception e) {
                log.warn("WhatsApp failed, using email");
                emailService.sendWelcome(
                        event.getRecipientEmail(),
                        event.getRecipientName()
                );
            }
        } else {
            emailService.sendWelcome(
                    event.getRecipientEmail(),
                    event.getRecipientName()
            );
        }
    }

    private void sendPayment(NotificationEvent event) {
        if (event.getPriority() == 1) {
            try {
                whatsAppService.sendPaymentConfirmation(
                        event.getRecipientPhone(),
                        event.getTemplateData()
                );
            } catch (Exception e) {
                emailService.sendPaymentConfirmation(
                        event.getRecipientEmail(),
                        event.getTemplateData()
                );
            }
        } else {
            emailService.sendPaymentConfirmation(
                    event.getRecipientEmail(),
                    event.getTemplateData()
            );
        }
    }
}
