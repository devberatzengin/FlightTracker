package org.devberat.controller.Impl;

import lombok.RequiredArgsConstructor;
import org.devberat.model.Notification;
import org.devberat.model.RootEntity;
import org.devberat.service.INotificationService;
import org.devberat.service.ISecurityService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rest/api/notifications")
@RequiredArgsConstructor
public class NotificationController extends RestBaseController {

    private final INotificationService notificationService;
    private final ISecurityService securityService;

    @GetMapping
    public RootEntity<List<Notification>> getNotifications() {
        return ok(notificationService.getMyNotifications(securityService.getCurrentUser().getId()));
    }

    @GetMapping("/unread-count")
    public RootEntity<Long> getUnreadCount() {
        return ok(notificationService.getUnreadCount(securityService.getCurrentUser().getId()));
    }

    @PutMapping("/{id}/read")
    public RootEntity<Boolean> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ok(true);
    }
}
