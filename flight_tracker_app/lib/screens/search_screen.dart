import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_staggered_animations/flutter_staggered_animations.dart';
import '../providers/flight_provider.dart';
import '../models/flight_model.dart';
import '../theme/app_theme.dart';
import 'flight_details_screen.dart';
import 'package:intl/intl.dart'; 

class SearchScreen extends StatefulWidget {
  const SearchScreen({Key? key}) : super(key: key);

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  String _searchQuery = '';

  @override
  Widget build(BuildContext context) {
    final flightProvider = Provider.of<FlightProvider>(context);
    List<Flight> displayedFlights = flightProvider.flights;

    if (_searchQuery.isNotEmpty) {
      final q = _searchQuery.toLowerCase();
      displayedFlights = displayedFlights.where((f) {
        return f.departureAirportName.toLowerCase().contains(q) || 
               f.arrivalAirportName.toLowerCase().contains(q) ||
               f.flightNumber.toLowerCase().contains(q);
      }).toList();
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Search Flights', style: TextStyle(letterSpacing: 1.2)),
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(80),
          child: Padding(
            padding: const EdgeInsets.fromLTRB(20, 0, 20, 20),
            child: TextField(
              style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 16, color: AppTheme.textPrimary),
              decoration: InputDecoration(
                hintText: 'Search by city, airport, or flight #',
                prefixIcon: const Icon(Icons.search, color: AppTheme.primary),
                suffixIcon: _searchQuery.isNotEmpty 
                  ? IconButton(
                      icon: const Icon(Icons.clear, color: AppTheme.textSecondary),
                      onPressed: () { /* Note: TextField needs a controller to clear text, but we keep this simple */ setState(() => _searchQuery = ''); },
                    )
                  : null,
              ),
              onChanged: (val) => setState(() => _searchQuery = val),
            ),
          ),
        ),
      ),
      body: displayedFlights.isEmpty 
        ? Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.search_off_rounded, size: 80, color: AppTheme.textSecondary.withOpacity(0.3)),
                const SizedBox(height: 16),
                const Text('No flights found.', style: TextStyle(color: AppTheme.textSecondary, fontSize: 18, fontWeight: FontWeight.bold)),
              ],
            ),
          )
        : AnimationLimiter(
            child: ListView.builder(
              physics: const BouncingScrollPhysics(),
              padding: const EdgeInsets.all(20),
              itemCount: displayedFlights.length,
              itemBuilder: (context, index) {
                final flight = displayedFlights[index];
                final heroTag = 'search_${flight.id}_$index';
                final timeFormat = DateFormat('h:mm a');
                final deptTime = flight.departureTime != null ? timeFormat.format(flight.departureTime!) : 'TBD';

                return AnimationConfiguration.staggeredList(
                  position: index,
                  duration: const Duration(milliseconds: 500),
                  child: SlideAnimation(
                    horizontalOffset: 50.0,
                    child: FadeInAnimation(
                      child: Container(
                        margin: const EdgeInsets.only(bottom: 16),
                        decoration: BoxDecoration(
                          color: AppTheme.surfaceHighlight,
                          borderRadius: BorderRadius.circular(20),
                          boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.2), blurRadius: 10, offset: const Offset(0, 5))],
                          border: Border.all(color: Colors.white.withOpacity(0.05)),
                        ),
                        child: Material(
                          color: Colors.transparent,
                          child: InkWell(
                            borderRadius: BorderRadius.circular(20),
                            onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => FlightDetailsScreen(flight: flight, heroTag: heroTag))),
                            child: Padding(
                              padding: const EdgeInsets.all(20),
                              child: Row(
                                children: [
                                  Container(
                                    padding: const EdgeInsets.all(12),
                                    decoration: BoxDecoration(
                                      color: AppTheme.primary.withOpacity(0.15),
                                      shape: BoxShape.circle,
                                    ),
                                    child: const Icon(Icons.flight_takeoff, color: AppTheme.primary),
                                  ),
                                  const SizedBox(width: 16),
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text('${flight.departureAirportName} ➔ ${flight.arrivalAirportName}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: AppTheme.textPrimary)),
                                        const SizedBox(height: 4),
                                        Text('Flight: ${flight.flightNumber} • Dep: $deptTime', style: const TextStyle(color: AppTheme.textSecondary, fontSize: 13, fontWeight: FontWeight.w500)),
                                      ],
                                    ),
                                  ),
                                  const Icon(Icons.chevron_right, color: AppTheme.textSecondary),
                                ],
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                );
              },
            ),
          ),
    );
  }
}
