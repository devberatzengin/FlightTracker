import 'package:flutter/material.dart';
import '../models/flight_model.dart';
import '../services/flight_service.dart';

class FlightProvider extends ChangeNotifier {
  final FlightService _flightService = FlightService();
  List<Flight> _flights = [];
  bool _isLoading = false;

  List<Flight> get flights => _flights;
  bool get isLoading => _isLoading;

  Future<void> fetchFlights() async {
    _isLoading = true;
    // We notify here if we want to show a spinner before the data comes in
    notifyListeners();

    _flights = await _flightService.getFlights();

    _isLoading = false;
    notifyListeners();
  }
}
