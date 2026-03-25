import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/flight_model.dart';
import '../utils/api_constants.dart';
import 'auth_service.dart';

class FlightService {
  final AuthService _authService = AuthService();

  Future<List<Flight>> getFlights() async {
    try {
      final token = await _authService.getToken();
      final headers = {
        'Content-Type': 'application/json',
      };
      
      if (token != null) {
        headers['Authorization'] = 'Bearer $token';
      }

      final response = await http.get(
        Uri.parse(ApiConstants.flightListEndpoint),
        headers: headers,
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        if (body['result'] == true && body['data'] != null) {
          final List<dynamic> data = body['data'];
          return data.map((json) => Flight.fromJson(json)).toList();
        }
      }
      return [];
    } catch (e) {
      print('Get Flights Error: $e');
      return [];
    }
  }
}
