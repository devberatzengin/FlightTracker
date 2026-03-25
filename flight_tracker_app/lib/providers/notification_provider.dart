import 'package:flutter/material.dart';
import '../models/notification_model.dart';
import '../services/notification_service.dart';

class NotificationProvider extends ChangeNotifier {
  final NotificationService _notificationService = NotificationService();
  List<NotificationModel> _notifications = [];
  int _unreadCount = 0;
  bool _isLoading = false;

  List<NotificationModel> get notifications => _notifications;
  int get unreadCount => _unreadCount;
  bool get isLoading => _isLoading;

  Future<void> fetchNotifications() async {
    _isLoading = true;
    notifyListeners();

    _notifications = await _notificationService.getNotifications();
    _unreadCount = await _notificationService.getUnreadCount();

    _isLoading = false;
    notifyListeners();
  }

  Future<void> markAsRead(String id) async {
    final success = await _notificationService.markAsRead(id);
    if (success) {
      _unreadCount = await _notificationService.getUnreadCount();
      // Update local list
      int index = _notifications.indexWhere((n) => n.id == id);
      if (index != -1) {
        // Technically we'd need to recreate the model if fields are final, but for simplicity:
        await fetchNotifications();
      }
    }
  }

  Future<void> refreshUnreadCount() async {
    _unreadCount = await _notificationService.getUnreadCount();
    notifyListeners();
  }
}
