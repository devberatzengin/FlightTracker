import 'dart:convert';
import 'package:http/http.dart' as http;
import '../utils/api_constants.dart';
import 'auth_service.dart';
import '../models/notification_model.dart';

class NotificationService {
  final AuthService _authService = AuthService();

  Future<Map<String, String>> _getHeaders() async {
    final token = await _authService.getToken();
    return {
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Bearer $token',
    };
  }

  Future<List<NotificationModel>> getNotifications() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConstants.baseUrl}/notifications'),
        headers: await _getHeaders(),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        if (body['result'] == true) {
          return (body['data'] as List)
              .map((n) => NotificationModel.fromJson(n))
              .toList();
        }
      }
      return [];
    } catch (e) {
      print('Get Notifications Error: $e');
      return [];
    }
  }

  Future<int> getUnreadCount() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConstants.baseUrl}/notifications/unread-count'),
        headers: await _getHeaders(),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        if (body['result'] == true) {
          return (body['data'] as num).toInt();
        }
      }
      return 0;
    } catch (e) {
      print('Get Unread Count Error: $e');
      return 0;
    }
  }

  Future<bool> markAsRead(String id) async {
    try {
      final response = await http.put(
        Uri.parse('${ApiConstants.baseUrl}/notifications/$id/read'),
        headers: await _getHeaders(),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        return body['result'] == true;
      }
      return false;
    } catch (e) {
      print('Mark As Read Error: $e');
      return false;
    }
  }
}
