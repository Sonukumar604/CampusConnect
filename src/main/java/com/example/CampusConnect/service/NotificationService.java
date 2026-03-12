package com.example.CampusConnect.service;


import com.example.CampusConnect.model.Notification;
import com.example.CampusConnect.model.NotificationType;
import com.example.CampusConnect.model.User;

import java.util.List;

public interface NotificationService {

    void createNotification(User user, String title, String message, NotificationType type);

    List<Notification> getUserNotifications(Long userId);

    void markAsRead(Long notificationId);

}