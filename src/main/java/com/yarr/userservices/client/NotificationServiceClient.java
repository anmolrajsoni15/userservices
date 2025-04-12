package com.yarr.userservices.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.yarr.userservices.dto.NotificationDTO;

@FeignClient(name = "notification-service", url = "${notification.service.url}")
public interface NotificationServiceClient {
    
    @PostMapping("/api/v1/notifications/create")
    void createNotification(@RequestBody NotificationDTO notificationDTO);
}
