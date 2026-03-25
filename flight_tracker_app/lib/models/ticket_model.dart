class Ticket {
  final String id;
  final String flightNumber;
  final String departureCity;
  final String arrivalCity;
  final String departureTime;
  final String arrivalTime;
  final String seatNumber;
  final String passengerName;
  final double price;
  final String pnrCode;
  final bool isCheckedIn;

  Ticket({
    required this.id,
    required this.flightNumber,
    required this.departureCity,
    required this.arrivalCity,
    required this.departureTime,
    required this.arrivalTime,
    required this.seatNumber,
    required this.passengerName,
    required this.price,
    required this.pnrCode,
    required this.isCheckedIn,
  });

  factory Ticket.fromJson(Map<String, dynamic> json) {
    return Ticket(
      id: json['id']?.toString() ?? '',
      flightNumber: json['flightNumber'] ?? 'N/A',
      departureCity: json['departureCity'] ?? json['fromCity'] ?? 'Unknown',
      arrivalCity: json['arrivalCity'] ?? json['toCity'] ?? 'Unknown',
      departureTime: json['departureTime'] ?? '00:00',
      arrivalTime: json['arrivalTime'] ?? '00:00',
      seatNumber: json['seatNumber'] ?? '??',
      passengerName: json['passengerName'] ?? 'Traveler',
      price: json['price'] is num ? (json['price'] as num).toDouble() : double.tryParse(json['price'].toString()) ?? 0.0,
      pnrCode: json['pnrCode'] ?? '',
      isCheckedIn: json['isCheckedIn'] ?? json['checkedIn'] ?? json['checked_in'] ?? false,
    );
  }
}