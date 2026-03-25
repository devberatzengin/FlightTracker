import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/ticket_model.dart';
import '../models/seat_map_model.dart';
import '../utils/api_constants.dart';
import 'auth_service.dart';

class TicketService {
  final AuthService _authService = AuthService();

  Future<Map<String, String>> _getHeaders() async {
    final token = await _authService.getToken();
    return {
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Bearer $token',
    };
  }

  Future<List<Ticket>> getMyTickets() async {
    try {
      final response = await http.get(
        Uri.parse(ApiConstants.myTicketsEndpoint),
        headers: await _getHeaders(),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        if (body['result'] == true && body['data'] != null) {
          final List<dynamic> data = body['data'];
          return data.map((json) => Ticket.fromJson(json)).toList();
        }
      }
      return [];
    } catch (e) {
      print('Get My Tickets Error: $e');
      return [];
    }
  }

  Future<List<SeatMap>> getSeatMap(String flightId) async {
    try {
      final response = await http.get(
        Uri.parse(ApiConstants.seatMapEndpoint(flightId)),
        headers: await _getHeaders(),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        final List<dynamic> data = body['data'] ?? [];
        return data.map((s) => SeatMap.fromJson(s)).toList();
      }
      return [];
    } catch (e) {
      print('Get Seat Map Error: $e');
      return [];
    }
  }

  Future<String?> cancelTicket(String ticketId) async {
    try {
      final token = await AuthService().getToken();
      final response = await http.put(
        Uri.parse('${ApiConstants.baseUrl}/ticket/cancel/$ticketId'),
        headers: {
          'Authorization': 'Bearer $token',
        },
      );

      if (response.statusCode == 200) {
        return 'Ticket cancelled successfully.';
      }
      return 'Failed to cancel ticket.';
    } catch (e) {
      return 'Error occurred during cancellation.';
    }
  }

  Future<String?> bookTicket(String flightId, String seatNumber, {bool useWallet = false}) async {
    try {
      final response = await http.post(
        Uri.parse(ApiConstants.bookTicketEndpoint),
        headers: await _getHeaders(),
        body: jsonEncode({
          'flightId': flightId,
          'seatNumber': seatNumber,
          'useWallet': useWallet,
        }),
      );

      print('Book Ticket Status: ${response.statusCode}');
      print('Book Ticket Response: ${response.body}');

      if (response.body.isEmpty) {
        return 'Server returned an empty response (Status: ${response.statusCode}).';
      }

      final body = jsonDecode(response.body);
      if (response.statusCode == 200 && body['result'] == true) {
        return null; // Success
      }
      return body['errorMessage'] ?? body['message'] ?? 'Booking failed.';
    } catch (e) {
      print('Book Ticket Error: $e');
      return 'An error occurred during booking: $e';
    }
  }

  Future<String?> checkIn(String pnrCode) async {
    try {
      final response = await http.post(
        Uri.parse(ApiConstants.checkInEndpoint(pnrCode)),
        headers: await _getHeaders(),
      );

      if (response.statusCode == 200) {
        final body = jsonDecode(response.body);
        if (body['result'] == true) {
          return "Check-in successful!";
        }
      }
      return null;
    } catch (e) {
      print('Check In Error: $e');
      return null;
    }
  }
}
