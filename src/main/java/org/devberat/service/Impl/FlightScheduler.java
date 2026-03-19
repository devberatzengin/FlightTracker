package org.devberat.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devberat.model.Flight;
import org.devberat.model.FlightStatus;
import org.devberat.repository.IFlightRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightScheduler {

    private final IFlightRepository flightRepository;

    // Runs every 1 minute (fixedRate = 60000 ms)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateFlightStatuses() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fifteenMinsLater = now.plusMinutes(15);

        // 1. If less than 15 minutes left to departure: SCHEDULED -> WAITING_FOR_PERMISSION
        List<Flight> toWaitPermission = flightRepository.findByStatusAndDepartureTimeBetween(
                FlightStatus.SCHEDULED, now, fifteenMinsLater);

        for (Flight flight : toWaitPermission) {
            flight.setStatus(FlightStatus.WAITING_FOR_PERMISSION);
            log.info("Flight {}: Departure is imminent, waiting for tower permission.", flight.getFlightNumber());
        }

        // 2. If departure time has arrived: WAITING_FOR_PERMISSION -> IN_AIR
        List<Flight> toStart = flightRepository.findByStatusAndDepartureTimeBefore(
                FlightStatus.WAITING_FOR_PERMISSION, now);

        for (Flight flight : toStart) {
            flight.setStatus(FlightStatus.IN_AIR);
            log.info("Flight {}: Permission granted and took off. Status: IN_AIR", flight.getFlightNumber());
        }

        // 3. If arrival time has passed: IN_AIR -> LANDED
        List<Flight> toLand = flightRepository.findByStatusAndArrivalTimeBefore(
                FlightStatus.IN_AIR, now);

        for (Flight flight : toLand) {
            flight.setStatus(FlightStatus.LANDED);
            log.info("Flight {}: Successfully landed at the destination. Status: LANDED", flight.getFlightNumber());
        }

        // Save all changes in bulk
        if (!toWaitPermission.isEmpty()) flightRepository.saveAll(toWaitPermission);
        if (!toStart.isEmpty()) flightRepository.saveAll(toStart);
        if (!toLand.isEmpty()) flightRepository.saveAll(toLand);
    }
}