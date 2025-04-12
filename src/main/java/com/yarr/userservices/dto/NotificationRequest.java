package com.yarr.userservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String message;
    private String type;
    private String recipientRole;
    private String recipientId;
    private String senderId;
    private String senderName;
    private String referenceId;
    private String referenceType;
}
