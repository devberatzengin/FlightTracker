import 'dart:convert';
import 'package:http/http.dart' as http;
import '../utils/api_constants.dart';
import 'auth_service.dart';

class WeatherService {
  final AuthService _authService = AuthService();

  Future<Map<String, String>> _getHeaders() async {
    final token = await _authService.getToken();
    return {
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Bearer $token',
    };
  }

  Future<Map<String, dynamic>?> getWeather(String city) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConstants.baseUrl}/weather/$city'),
        headers: await _getHeaders(),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        if (body['result'] == true) {
          return body['data'];
        }
      }
      return null;
    } catch (e) {
      print('Get Weather Error: $e');
      return null;
    }
  }
}
