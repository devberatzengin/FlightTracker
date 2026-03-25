import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../models/flight_model.dart';
import '../theme/app_theme.dart';
import '../services/ticket_service.dart';

class FlightDetailsScreen extends StatelessWidget {
  final Flight flight;
  final String heroTag;
  const FlightDetailsScreen({Key? key, required this.flight, required this.heroTag}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final timeFormat = DateFormat('MMM d, yyyy - h:mm a');
    final deptTime = flight.departureTime != null ? timeFormat.format(flight.departureTime!) : 'N/A';
    final arrTime = flight.arrivalTime != null ? timeFormat.format(flight.arrivalTime!) : 'N/A';

    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        iconTheme: const IconThemeData(color: Colors.white),
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Hero(
              tag: heroTag,
              child: Material(
                type: MaterialType.transparency,
                child: Container(
                  decoration: BoxDecoration(
                    color: AppTheme.surfaceHighlight,
                    borderRadius: const BorderRadius.only(
                      bottomLeft: Radius.circular(40),
                      bottomRight: Radius.circular(40),
                    ),
                    boxShadow: [
                      BoxShadow(color: AppTheme.primary.withOpacity(0.2), blurRadius: 40, offset: const Offset(0, 20)),
                    ],
                    border: Border.all(color: Colors.white.withOpacity(0.05), width: 1),
                  ),
                  padding: const EdgeInsets.only(top: 110, left: 24, right: 24, bottom: 32),
                  child: SingleChildScrollView(
                    physics: const NeverScrollableScrollPhysics(),
                    child: Column(
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                          decoration: BoxDecoration(
                            color: AppTheme.surface,
                            borderRadius: BorderRadius.circular(20),
                            border: Border.all(color: AppTheme.primary.withOpacity(0.4)),
                          ),
                          child: Text(
                            flight.flightNumber, 
                            style: const TextStyle(color: AppTheme.primary, fontWeight: FontWeight.bold, fontSize: 16, letterSpacing: 1.5)
                          ),
                        ),
                        const SizedBox(height: 50),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text('DEPARTURE', style: TextStyle(color: AppTheme.textSecondary, fontSize: 12, fontWeight: FontWeight.bold, letterSpacing: 2)),
                                  const SizedBox(height: 8),
                                  Text(flight.departureAirportName, style: Theme.of(context).textTheme.displayLarge),
                                ],
                              ),
                            ),
                            Container(
                              padding: const EdgeInsets.all(16),
                              decoration: BoxDecoration(
                                shape: BoxShape.circle,
                                color: AppTheme.primary.withOpacity(0.1),
                                boxShadow: [BoxShadow(color: AppTheme.primary.withOpacity(0.2), blurRadius: 20)],
                              ),
                              child: const Icon(Icons.flight_outlined, color: AppTheme.primary, size: 36),
                            ),
                          ],
                        ),
                        const SizedBox(height: 40),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text('ARRIVAL', style: TextStyle(color: AppTheme.textSecondary, fontSize: 12, fontWeight: FontWeight.bold, letterSpacing: 2)),
                                  const SizedBox(height: 8),
                                  Text(flight.arrivalAirportName, style: Theme.of(context).textTheme.displayLarge),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Flight Information', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppTheme.textPrimary)),
                  const SizedBox(height: 24),
                  Container(
                    decoration: BoxDecoration(
                      color: AppTheme.surface,
                      borderRadius: BorderRadius.circular(24),
                      border: Border.all(color: AppTheme.surfaceHighlight, width: 2),
                      boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.3), blurRadius: 20, offset: const Offset(0, 10))],
                    ),
                    padding: const EdgeInsets.all(24),
                    child: Column(
                      children: [
                        _InfoRow(icon: Icons.access_time_filled, title: 'Departure Time', value: deptTime),
                        const Padding(padding: EdgeInsets.symmetric(vertical: 16), child: Divider(height: 1, thickness: 1, color: AppTheme.surfaceHighlight)),
                        _InfoRow(icon: Icons.flight_land, title: 'Arrival Time', value: arrTime),
                        const Padding(padding: EdgeInsets.symmetric(vertical: 16), child: Divider(height: 1, thickness: 1, color: AppTheme.surfaceHighlight)),
                        _InfoRow(icon: Icons.info, title: 'Status', value: flight.status ?? 'Unknown', valueColor: _getStatusColor(flight.status)),
                        const Padding(padding: EdgeInsets.symmetric(vertical: 16), child: Divider(height: 1, thickness: 1, color: AppTheme.surfaceHighlight)),
                        _InfoRow(icon: Icons.airplanemode_active, title: 'Aircraft Model', value: flight.aircraftModel ?? 'N/A'),
                        const Padding(padding: EdgeInsets.symmetric(vertical: 16), child: Divider(height: 1, thickness: 1, color: AppTheme.surfaceHighlight)),
                        _InfoRow(icon: Icons.person, title: 'Captain', value: flight.captainFullName ?? 'N/A'),
                        const Padding(padding: EdgeInsets.symmetric(vertical: 16), child: Divider(height: 1, thickness: 1, color: AppTheme.surfaceHighlight)),
                        _InfoRow(icon: Icons.airline_seat_recline_normal, title: 'Occupancy', value: flight.currentOccupancy != null ? '${flight.currentOccupancy} seats' : 'N/A'),
                      ],
                    ),
                  ),
                  const SizedBox(height: 48),
                  AnimatedContainer(
                      duration: const Duration(milliseconds: 300),
                      height: 64,
                      width: double.infinity,
                      decoration: BoxDecoration(
                        gradient: const LinearGradient(
                          colors: [AppTheme.primary, Color(0xFF818CF8)],
                          begin: Alignment.centerLeft,
                          end: Alignment.centerRight,
                        ),
                        borderRadius: BorderRadius.circular(20),
                        boxShadow: [
                          BoxShadow(color: AppTheme.primary.withOpacity(0.4), blurRadius: 20, offset: const Offset(0, 10)),
                        ],
                      ),
                      child: ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.transparent,
                          shadowColor: Colors.transparent,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                        ),
                        onPressed: () => _showBookingDialog(context),
                        child: const Text('Book Flight', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.white, letterSpacing: 1.2)),
                      ),
                    ),
                    const SizedBox(height: 60),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Color _getStatusColor(String? status) {
    switch (status?.toUpperCase()) {
      case 'CANCELLED':
        return AppTheme.error;
      case 'DELAYED':
        return AppTheme.warning;
      case 'LANDED':
        return AppTheme.success;
      default:
        return AppTheme.accent;
    }
  }

  void _showBookingDialog(BuildContext context) {
    final TextEditingController seatController = TextEditingController();
    bool useWallet = false;
    bool isLoading = false;

    showDialog(
      context: context,
      builder: (ctx) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              backgroundColor: AppTheme.surfaceHighlight,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
              title: const Text('Book Your Next Flight', style: TextStyle(color: AppTheme.textPrimary, fontWeight: FontWeight.bold)),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  TextField(
                    controller: seatController,
                    style: const TextStyle(color: AppTheme.textPrimary, fontSize: 18),
                    decoration: const InputDecoration(
                      labelText: 'Seat Number (e.g., 12A)',
                      prefixIcon: Icon(Icons.airline_seat_recline_normal, color: AppTheme.primary),
                    ),
                  ),
                  const SizedBox(height: 16),
                  SwitchListTile(
                    title: const Text('Use SkyWallet Balance', style: TextStyle(color: AppTheme.textPrimary, fontWeight: FontWeight.w600)),
                    activeColor: AppTheme.primary,
                    value: useWallet,
                    onChanged: (val) => setState(() => useWallet = val),
                  ),
                ],
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.pop(ctx),
                  child: const Text('Cancel', style: TextStyle(color: AppTheme.textSecondary)),
                ),
                ElevatedButton(
                  onPressed: isLoading ? null : () async {
                    if (seatController.text.isEmpty) {
                      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Enter a seat number', style: TextStyle(color: Colors.white)), backgroundColor: AppTheme.error));
                      return;
                    }

                    setState(() => isLoading = true);
                    final error = await TicketService().bookTicket(flight.id, seatController.text, useWallet: useWallet);
                    if (!context.mounted) return;
                    
                    if (error == null) {
                      Navigator.pop(ctx);
                      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Flight booked successfully!', style: TextStyle(color: Colors.white)), backgroundColor: AppTheme.success));
                    } else {
                      setState(() => isLoading = false);
                      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(error, style: const TextStyle(color: Colors.white)), backgroundColor: AppTheme.error));
                    }
                  },
                  child: isLoading ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2)) : const Text('Confirm Booking'),
                ),
              ],
            );
          },
        );
      },
    );
  }
}

class _InfoRow extends StatelessWidget {
  final IconData icon;
  final String title;
  final String value;
  final Color? valueColor;
  const _InfoRow({Key? key, required this.icon, required this.title, required this.value, this.valueColor}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(color: AppTheme.primary.withOpacity(0.1), borderRadius: BorderRadius.circular(14)),
          child: Icon(icon, color: AppTheme.primary, size: 22),
        ),
        const SizedBox(width: 16),
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, style: const TextStyle(color: AppTheme.textSecondary, fontSize: 13, fontWeight: FontWeight.w500)),
            const SizedBox(height: 4),
            Text(value, style: TextStyle(fontWeight: FontWeight.w800, fontSize: 16, color: valueColor ?? AppTheme.textPrimary)),
          ],
        ),
      ],
    );
  }
}
