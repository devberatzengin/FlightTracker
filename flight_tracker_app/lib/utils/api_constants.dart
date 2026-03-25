class ApiConstants {
  // MacBook Air M3 ve macOS Desktop için localhost:8080
  static const String baseUrl = 'http://localhost:8080/rest/api';
  
  // Auth Endpoints
  static const String loginEndpoint = '$baseUrl/auth/login';
  static const String registerEndpoint = '$baseUrl/auth/register';
  static const String userProfileEndpoint = '$baseUrl/user/me';

  // Flight & Ticket Endpoints
  static const String flightListEndpoint = '$baseUrl/flight/list';
  static const String bookTicketEndpoint = '$baseUrl/ticket/book';
  static const String myTicketsEndpoint = '$baseUrl/ticket/my-tickets';
  
  // İptal işlemi için baz URL (Daha önce hata aldığın yer burası)
  static const String ticketCancelBase = '$baseUrl/ticket/cancel';

  // Fonksiyonel Endpoints
  static String seatMapEndpoint(String flightId) => '$baseUrl/ticket/flight/$flightId/seats';
  static String checkInEndpoint(String pnrCode) => '$baseUrl/ticket/check-in/$pnrCode';
}