import 'dart:convert';
import 'package:http/http.dart' as http;
import '../utils/api_constants.dart';
import 'auth_service.dart';

class WalletService {
  final AuthService _authService = AuthService();

  Future<Map<String, String>> _getHeaders() async {
    final token = await _authService.getToken();
    return {
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Bearer $token',
    };
  }

  Future<double?> getBalance() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConstants.baseUrl}/wallet/balance'),
        headers: await _getHeaders(),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        if (body['result'] == true) {
          return (body['data'] as num).toDouble();
        }
      }
      return null;
    } catch (e) {
      print('Get Balance Error: $e');
      return null;
    }
  }

  Future<bool> addFunds(double amount) async {
    try {
      // Amount is sent as a query parameter in WalletController.java
      final response = await http.post(
        Uri.parse('${ApiConstants.baseUrl}/wallet/add-funds?amount=$amount'),
        headers: await _getHeaders(),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        return body['result'] == true;
      }
      return false;
    } catch (e) {
      print('Add Funds Error: $e');
      return false;
    }
  }
}
