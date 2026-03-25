import 'package:flutter/material.dart';
import '../models/ticket_model.dart';
import '../models/seat_map_model.dart';
import '../services/ticket_service.dart';

class TicketProvider with ChangeNotifier {
  final TicketService _ticketService = TicketService();

  List<Ticket> _myTickets = [];
  List<Ticket> get myTickets => _myTickets;

  List<SeatMap> _currentSeatMap = [];
  List<SeatMap> get currentSeatMap => _currentSeatMap;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  Future<void> fetchMyTickets() async {
    _isLoading = true;
    notifyListeners();

    _myTickets = await _ticketService.getMyTickets();

    _isLoading = false;
    notifyListeners();
  }

  Future<void> fetchSeatMap(String flightId) async {
    _isLoading = true;
    notifyListeners();

    _currentSeatMap = await _ticketService.getSeatMap(flightId);

    _isLoading = false;
    notifyListeners();
  }

  Future<String?> bookTicket(String flightId, String seatNumber, {bool useWallet = false}) async {
    _isLoading = true;
    notifyListeners();

    String? error = await _ticketService.bookTicket(flightId, seatNumber, useWallet: useWallet);
    if (error == null) {
      await fetchMyTickets(); // refresh tickets
    }

    _isLoading = false;
    notifyListeners();
    return error;
  }

  Future<String?> checkIn(String pnrCode) async {
    _setLoading(true);
    final message = await _ticketService.checkIn(pnrCode);
    await fetchMyTickets();
    _setLoading(false);
    return message;
  }

  Future<String?> cancelTicket(String ticketId) async {
    _setLoading(true);
    final message = await _ticketService.cancelTicket(ticketId);
    await fetchMyTickets();
    _setLoading(false);
    return message;
  }

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }
}
