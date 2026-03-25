import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import '../providers/auth_provider.dart';
import '../providers/flight_provider.dart';
import '../models/flight_model.dart';
import '../theme/app_theme.dart';
import 'flight_details_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<FlightProvider>(context, listen: false).fetchFlights();
    });
  }

  @override
  Widget build(BuildContext context) {
    final flightProvider = Provider.of<FlightProvider>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Live Flights', style: TextStyle(letterSpacing: 1.2)),
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_none_rounded, size: 28),
            onPressed: () {},
          ),
          IconButton(
            icon: const Icon(Icons.logout_rounded, color: AppTheme.error),
            onPressed: () async {
              await Provider.of<AuthProvider>(context, listen: false).logout();
              if (mounted) Navigator.pushReplacementNamed(context, '/auth');
            },
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: RefreshIndicator(
        color: AppTheme.primary,
        backgroundColor: AppTheme.surfaceHighlight,
        onRefresh: () async {
          await flightProvider.fetchFlights();
        },
        child: flightProvider.isLoading
            ? const Center(child: CircularProgressIndicator(color: AppTheme.primary))
            : flightProvider.flights.isEmpty
                ? ListView(
                    children: [
                      const SizedBox(height: 200),
                      Center(
                        child: Column(
                          children: [
                            Icon(Icons.airplanemode_inactive, size: 100, color: AppTheme.textSecondary.withOpacity(0.2)),
                            const SizedBox(height: 24),
                            Text(
                              'No flights currently active.',
                              style: Theme.of(context).textTheme.titleLarge?.copyWith(color: AppTheme.textSecondary),
                            ),
                          ],
                        ),
                      ),
                    ],
                  )
                : AnimationLimiter(
                    child: ListView.builder(
                      physics: const BouncingScrollPhysics(parent: AlwaysScrollableScrollPhysics()),
                      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
                      itemCount: flightProvider.flights.length,
                      itemBuilder: (context, index) {
                        final flight = flightProvider.flights[index];
                        return AnimationConfiguration.staggeredList(
                          position: index,
                          duration: const Duration(milliseconds: 700),
                          child: SlideAnimation(
                            verticalOffset: 60.0,
                            child: FadeInAnimation(
                              child: FlightCard(flight: flight, index: index),
                            ),
                          ),
                        );
                      },
                    ),
                  ),
      ),
    );
  }
}

class FlightCard extends StatelessWidget {
  final Flight flight;
  final int index;
  const FlightCard({Key? key, required this.flight, required this.index}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final timeFormat = DateFormat('MMM d, h:mm a');
    final deptTime = flight.departureTime != null ? timeFormat.format(flight.departureTime!) : 'Unknown';
    final arrTime = flight.arrivalTime != null ? timeFormat.format(flight.arrivalTime!) : 'Unknown';
    
    final heroTag = 'flight_card_${flight.id}_$index';

    return GestureDetector(
      onTap: () {
        Navigator.push(
          context,
          PageRouteBuilder(
            transitionDuration: const Duration(milliseconds: 700),
            reverseTransitionDuration: const Duration(milliseconds: 600),
            pageBuilder: (_, __, ___) => FlightDetailsScreen(flight: flight, heroTag: heroTag),
            transitionsBuilder: (_, animation, __, child) {
              return FadeTransition(opacity: animation, child: child);
            },
          ),
        );
      },
      child: Hero(
        tag: heroTag,
        flightShuttleBuilder: (flightContext, animation, direction, fromContext, toContext) {
          return DefaultTextStyle(
            style: DefaultTextStyle.of(toContext).style,
            child: toContext.widget,
          );
        },
        child: Material(
          type: MaterialType.transparency,
          child: Container(
            margin: const EdgeInsets.only(bottom: 24),
            decoration: BoxDecoration(
              color: AppTheme.surfaceHighlight,
              borderRadius: BorderRadius.circular(28),
              border: Border.all(color: Colors.white.withOpacity(0.05), width: 1),
              boxShadow: [
                BoxShadow(color: Colors.black.withOpacity(0.3), blurRadius: 20, offset: const Offset(0, 10)),
              ],
            ),
            padding: const EdgeInsets.all(24),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                      decoration: BoxDecoration(
                        color: AppTheme.surface,
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(color: AppTheme.primary.withOpacity(0.3)),
                      ),
                      child: Row(
                        children: [
                          const Icon(Icons.flight, size: 16, color: AppTheme.primary),
                          const SizedBox(width: 8),
                          Text(
                            flight.flightNumber,
                            style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 13, color: AppTheme.textPrimary, letterSpacing: 1.1),
                          ),
                        ],
                      ),
                    ),
                    Text(
                      flight.status ?? 'SCHEDULED',
                      style: TextStyle(
                        color: _getStatusColor(flight.status), 
                        fontWeight: FontWeight.w800, 
                        fontSize: 13, 
                        letterSpacing: 1.5
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 32),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            flight.departureAirportName.isEmpty ? 'MUC' : flight.departureAirportName,
                            style: Theme.of(context).textTheme.headlineMedium,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                          const SizedBox(height: 8),
                          Text(deptTime, style: const TextStyle(color: AppTheme.textSecondary, fontSize: 13, fontWeight: FontWeight.w600)),
                        ],
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: Column(
                        children: [
                          const Icon(Icons.flight_takeoff_rounded, color: AppTheme.accent, size: 32),
                          const SizedBox(height: 4),
                          Text('Direct', style: TextStyle(color: AppTheme.textSecondary.withOpacity(0.6), fontSize: 10, fontWeight: FontWeight.bold)),
                        ],
                      ),
                    ),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.end,
                        children: [
                          Text(
                            flight.arrivalAirportName.isEmpty ? 'JFK' : flight.arrivalAirportName,
                            style: Theme.of(context).textTheme.headlineMedium,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                          const SizedBox(height: 8),
                          Text(arrTime, style: const TextStyle(color: AppTheme.textSecondary, fontSize: 13, fontWeight: FontWeight.w600)),
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
}
