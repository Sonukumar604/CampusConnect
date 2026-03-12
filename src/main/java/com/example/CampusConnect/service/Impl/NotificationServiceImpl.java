package com.example.CampusConnect.service.Impl;


import com.example.CampusConnect.model.Notification;
import com.example.CampusConnect.model.NotificationType;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.NotificationRepository;
import com.example.CampusConnect.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(User user,
                                   String title,
                                   String message,
                                   NotificationType type) {

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .read(false)
                .createdAt(Instant.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUserNotifications(Long userId) {

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);

        notificationRepository.save(notification);
    }
}