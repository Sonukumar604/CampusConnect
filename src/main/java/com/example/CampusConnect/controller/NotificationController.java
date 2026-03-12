package com.example.CampusConnect.controller;

import com.example.CampusConnect.model.Notification;
import com.example.CampusConnect.security.CustomUserDetails;
import com.example.CampusConnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<Notification> getMyNotifications(Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return notificationService.getUserNotifications(userDetails.getUser().getId());
    }

    @PostMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {

        notificationService.markAsRead(id);
    }
}