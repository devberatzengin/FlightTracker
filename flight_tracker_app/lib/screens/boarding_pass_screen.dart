import 'package:flutter/material.dart';
import '../models/ticket_model.dart';
import 'package:intl/intl.dart';
import '../theme/app_theme.dart';

class BoardingPassScreen extends StatelessWidget {
  final Ticket ticket;
  const BoardingPassScreen({Key? key, required this.ticket}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Boarding Pass', style: TextStyle(letterSpacing: 1.2)),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        physics: const BouncingScrollPhysics(),
        child: Container(
          decoration: BoxDecoration(
            color: AppTheme.surfaceHighlight,
            borderRadius: BorderRadius.circular(32),
            boxShadow: [
              BoxShadow(color: Colors.black.withOpacity(0.5), blurRadius: 30, offset: const Offset(0, 15)),
            ],
            border: Border.all(color: Colors.white.withOpacity(0.05)),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    colors: [AppTheme.primary, Color(0xFF818CF8)],
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                  ),
                  borderRadius: BorderRadius.vertical(top: Radius.circular(32)),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          ticket.departureCity.substring(0, 3).toUpperCase(),
                          style: const TextStyle(fontSize: 40, fontWeight: FontWeight.w900, color: Colors.white, letterSpacing: 2),
                        ),
                        const SizedBox(height: 4),
                        Text(ticket.departureCity, style: TextStyle(color: Colors.white.withOpacity(0.8), fontSize: 12, fontWeight: FontWeight.w600)),
                      ],
                    ),
                    const RotatedBox(
                      quarterTurns: 1,
                      child: Icon(Icons.flight, color: Colors.white, size: 40),
                    ),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        Text(
                          ticket.arrivalCity.substring(0, 3).toUpperCase(),
                          style: const TextStyle(fontSize: 40, fontWeight: FontWeight.w900, color: Colors.white, letterSpacing: 2),
                        ),
                        const SizedBox(height: 4),
                        Text(ticket.arrivalCity, style: TextStyle(color: Colors.white.withOpacity(0.8), fontSize: 12, fontWeight: FontWeight.w600)),
                      ],
                    ),
                  ],
                ),
              ),
              Container(
                color: AppTheme.surface,
                padding: const EdgeInsets.all(32),
                child: Column(
                  children: [
                    _InfoRow('PASSENGER', ticket.passengerName.toUpperCase(), 'FLIGHT', ticket.flightNumber),
                    const SizedBox(height: 32),
                    _InfoRow('DATE', DateFormat('MMM dd, yyyy').format(DateTime.now()), 'SEAT', ticket.seatNumber),
                    const SizedBox(height: 32),
                    const _InfoRow('GATE', 'TBD', 'BOARDING TIME', '08:45 AM'),
                  ],
                ),
              ),
              Stack(
                children: [
                  const Divider(height: 1, color: AppTheme.background, thickness: 2),
                  Positioned(
                    left: -10,
                    top: -10,
                    child: Container(width: 20, height: 20, decoration: const BoxDecoration(color: AppTheme.background, shape: BoxShape.circle)),
                  ),
                  Positioned(
                    right: -10,
                    top: -10,
                    child: Container(width: 20, height: 20, decoration: const BoxDecoration(color: AppTheme.background, shape: BoxShape.circle)),
                  ),
                ],
              ),
              Container(
                padding: const EdgeInsets.all(40),
                decoration: const BoxDecoration(
                  color: AppTheme.surfaceHighlight,
                  borderRadius: BorderRadius.vertical(bottom: Radius.circular(32)),
                ),
                child: Column(
                  children: [
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(16)),
                      child: const Icon(Icons.qr_code_2, size: 160, color: Colors.black),
                    ),
                    const SizedBox(height: 24),
                    Text(ticket.pnrCode, style: const TextStyle(fontSize: 28, letterSpacing: 10, fontWeight: FontWeight.w900, color: AppTheme.textPrimary)),
                    const SizedBox(height: 12),
                    const Text('Scan at the gate', style: TextStyle(color: AppTheme.textSecondary, fontWeight: FontWeight.w600, letterSpacing: 1.5)),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _InfoRow extends StatelessWidget {
  final String label1, value1, label2, value2;
  const _InfoRow(this.label1, this.value1, this.label2, this.value2);

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label1, style: const TextStyle(color: AppTheme.textSecondary, fontSize: 11, fontWeight: FontWeight.bold, letterSpacing: 1.5)),
              const SizedBox(height: 6),
              Text(value1, style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 16, color: AppTheme.textPrimary)),
            ],
          ),
        ),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(label2, style: const TextStyle(color: AppTheme.textSecondary, fontSize: 11, fontWeight: FontWeight.bold, letterSpacing: 1.5)),
              const SizedBox(height: 6),
              Text(value2, style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 16, color: AppTheme.textPrimary)),
            ],
          ),
        ),
      ],
    );
  }
}
