import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../utils/api_constants.dart';
import '../models/user_model.dart';

class AuthService {
  static const String _tokenKey = 'jwt_token';

  // Giriş Yapma
  Future<bool> login(String email, String password) async {
    try {
      final response = await http.post(
        Uri.parse(ApiConstants.loginEndpoint),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'email': email, 'password': password}),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        // Backend RootEntity kullanıyorsa 'result' ve 'data' kontrolü
        if (body['result'] == true && body['data'] != null) {
          final token = body['data']['token'];
          await _saveToken(token);
          return true;
        }
      }
      return false;
    } catch (e) {
      print('Login Error: $e');
      return false;
    }
  }

  // Profil Bilgilerini Çekme
  Future<User?> getUserProfile() async {
    try {
      final token = await getToken();
      if (token == null) return null;

      final response = await http.get(
        Uri.parse(ApiConstants.userProfileEndpoint),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        // Backend direkt User nesnesi veya RootEntity içinde dönebilir
        if (body['id'] != null) {
          return User.fromJson(body);
        } else if (body['data'] != null) {
          return User.fromJson(body['data']);
        }
      }
      return null;
    } catch (e) {
      print('Get User Profile Error: $e');
      return null;
    }
  }

  // Kayıt Olma
  Future<bool> register(String firstName, String lastName, String email, String password, String phoneNumber) async {
    try {
      final response = await http.post(
        Uri.parse(ApiConstants.registerEndpoint),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'firstName': firstName,
          'lastName': lastName,
          'email': email,
          'password': password,
          'phoneNumber': phoneNumber
        }),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        return body['result'] == true;
      }
      return false;
    } catch (e) {
      print('Register Error: $e');
      return false;
    }
  }

  // Token Yönetimi
  Future<void> _saveToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_tokenKey, token);
  }

  Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_tokenKey);
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_tokenKey);
  }
}