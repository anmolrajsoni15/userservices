package com.yarr.userservices.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private UUID id;
    private String message;
    private String type;
    private String recipientId;
    private String recipientRole;
    private String senderId;
    private String senderName;
    private String referenceId;
    private String referenceType;
    private boolean read;
    private Instant createdAt;
}
