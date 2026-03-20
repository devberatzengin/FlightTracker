package org.devberat.service;

import org.devberat.model.Notification;
import org.devberat.model.User;
import java.util.List;
import java.util.UUID;

public interface INotificationService {
    void sendNotification(User user, String message, String type);
    List<Notification> getMyNotifications(UUID userId);
    Long getUnreadCount(UUID userId);
    void markAsRead(UUID id);
}
