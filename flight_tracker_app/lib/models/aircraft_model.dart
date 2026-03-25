class Aircraft {
  final String id;
  final String model;
  final String serialNumber;
  final int seatCapacity;
  final bool inService;

  Aircraft({
    required this.id,
    required this.model,
    required this.serialNumber,
    required this.seatCapacity,
    required this.inService,
  });

  factory Aircraft.fromJson(Map<String, dynamic> json) {
    return Aircraft(
      id: json['id'] ?? '',
      model: json['model'] ?? '',
      serialNumber: json['serialNumber'] ?? '',
      seatCapacity: json['seatCapacity'] ?? 0,
      inService: json['inService'] ?? false,
    );
  }
}
