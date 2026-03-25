class SeatMap {
  final String seatNumber;
  final bool isAvailable;

  SeatMap({
    required this.seatNumber,
    required this.isAvailable,
  });

  factory SeatMap.fromJson(Map<String, dynamic> json) {
  return SeatMap(
    seatNumber: json['seatNumber'] ?? '',
    isAvailable: json['available'] ?? false, 
  );
}
}
