class Airport {
  final String id;
  final String iataCode;
  final String name;
  final String city;
  final String country;
  final bool isActive;

  Airport({
    required this.id,
    required this.iataCode,
    required this.name,
    required this.city,
    required this.country,
    required this.isActive,
  });

  factory Airport.fromJson(Map<String, dynamic> json) {
    return Airport(
      id: json['id'] ?? '',
      iataCode: json['iataCode'] ?? '',
      name: json['name'] ?? '',
      city: json['city'] ?? '',
      country: json['country'] ?? '',
      isActive: json['isActive'] ?? false,
    );
  }
}
