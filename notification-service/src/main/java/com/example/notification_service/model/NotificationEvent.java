package com.example.notification_service.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String eventId;
    private String eventType; // WELCOME, PAYMENT_CONFIRMED, PROMOTION
    private String recipientPhone;
    private String recipientEmail;
    private String recipientName;
    private Map<String, String> templateData;
    private Integer priority; // 1=WhatsApp, 2=Email
    private LocalDateTime timestamp;
}

