class Flight {
  final String id;
  final String flightNumber;
  final String departureAirportName;
  final String arrivalAirportName;
  final String? aircraftModel;
  final String? captainFullName;
  final int? currentOccupancy;
  final String? status;
  final DateTime? departureTime;
  final DateTime? arrivalTime;
  final double basePrice;

  Flight({
    required this.id,
    required this.flightNumber,
    required this.departureAirportName,
    required this.arrivalAirportName,
    this.aircraftModel,
    this.captainFullName,
    this.currentOccupancy,
    this.status,
    this.departureTime,
    this.arrivalTime,
    this.basePrice = 0.0,
  });

  factory Flight.fromJson(Map<String, dynamic> json) {
    return Flight(
      id: json['id'] ?? '',
      flightNumber: json['flightNumber'] ?? '',
      departureAirportName: json['departureAirportName'] ?? '',
      arrivalAirportName: json['arrivalAirportName'] ?? '',
      aircraftModel: json['aircraftModel'],
      captainFullName: json['captainFullName'],
      currentOccupancy: json['currentOccupancy'],
      status: json['status'],
      departureTime: json['departureTime'] != null ? DateTime.parse(json['departureTime']) : null,
      arrivalTime: json['arrivalTime'] != null ? DateTime.parse(json['arrivalTime']) : null,
      basePrice: (json['basePrice'] ?? 0).toDouble(),
    );
  }
}
