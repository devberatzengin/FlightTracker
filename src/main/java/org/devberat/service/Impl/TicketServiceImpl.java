package org.devberat.service.Impl;

import lombok.RequiredArgsConstructor;
import org.devberat.DTO.SeatMapDto;
import org.devberat.DTO.TicketDto;
import org.devberat.exception.BaseException;
import org.devberat.exception.ErrorMessage;
import org.devberat.exception.MessageType;
import org.devberat.model.*;
import org.devberat.repository.IFlightRepository;
import org.devberat.repository.ITicketRepository;
import org.devberat.repository.IUserRepository;
import org.devberat.service.IPnrService;
import org.devberat.service.IPricingService;
import org.devberat.service.ISecurityService;
import org.devberat.service.ITicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements ITicketService {

    private final ITicketRepository ticketRepository;
    private final IFlightRepository flightRepository;
    private final IUserRepository userRepository;
    private final NotificationService notificationService;
    private final IPricingService pricingService;
    private final IPnrService pnrService;
    private final ISecurityService securityService;

    // PNR and Pricing logic moved to dedicated services.

    @Override
    public List<SeatMapDto.SeatInfo> getSeatMap(UUID flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Flight not found")));

        int capacity = flight.getAircraft().getSeatCapacity();
        List<String> occupiedSeats = ticketRepository.findByFlightIdAndStatus(flightId, TicketStatus.ACTIVE)
                .stream().map(Ticket::getSeatNumber).toList();

        List<SeatMapDto.SeatInfo> seatMap = new ArrayList<>();
        char[] rows = {'A', 'B', 'C', 'D', 'E', 'F'};

        for (int i = 1; i <= (capacity / 6) + 1; i++) {
            for (char row : rows) {
                if (seatMap.size() < capacity) {
                    String seatNum = i + String.valueOf(row);
                    seatMap.add(SeatMapDto.SeatInfo.builder()
                            .seatNumber(seatNum)
                            .isAvailable(!occupiedSeats.contains(seatNum))
                            .build());
                }
            }
        }
        return seatMap;
    }

    @Override
    @Transactional
    public TicketDto.Info bookTicket(TicketDto.BookingRequest request) {
        User currentUser = securityService.getCurrentUser();

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Flight not found")));

        if (flight.isFull()) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Flight is full!"));
        }

        if (ticketRepository.existsByFlightIdAndSeatNumberAndStatus(flight.getId(), request.getSeatNumber(), TicketStatus.ACTIVE)) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Seat " + request.getSeatNumber() + " is already taken!"));
        }

        BigDecimal finalPrice = pricingService.calculatePrice(flight);

        if (request.isUseWallet()) {
            try {
                currentUser.chargeBalance(finalPrice);
            } catch (RuntimeException e) {
                throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, 
                    "Insufficient wallet balance! Required: $" + finalPrice + ", Available: $" + (currentUser.getBalance() != null ? currentUser.getBalance() : "0")));
            }
        }

        // Feature 2: SkyMiles Reward (Gain 10% of price as miles)
        int earnedMiles = finalPrice.divide(new BigDecimal("10"), 0, java.math.RoundingMode.FLOOR).intValue();
        currentUser.addMiles(earnedMiles);
        userRepository.save(currentUser);

        Ticket ticket = new Ticket();
        ticket.setPnrCode(pnrService.generatePnr());
        ticket.setCheckedIn(false);
        ticket.setFlight(flight);
        ticket.setPassenger(currentUser);
        ticket.setSeatNumber(request.getSeatNumber());
        ticket.setPrice(finalPrice);
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setCreatedAt(new java.util.Date());

        flight.incrementOccupancy();
        flightRepository.save(flight);

        Ticket saved = ticketRepository.save(ticket);

        notificationService.sendNotification(currentUser, 
            "Sky journey booked! Flight: " + flight.getFlightNumber() + " to " + flight.getArrivalAirport().getName(), 
            "FLIGHT_BOOKED");

        return convertToDto(saved);
    }


    @Override
    @Transactional
    public void checkIn(String pnrCode) {
        Ticket ticket = ticketRepository.findByPnrCode(pnrCode)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "PNR not found")));

        if (ticket.isCheckedIn()) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Already checked in!"));
        }

        // Relaxed for testing: allow check-in anytime before departure.
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(ticket.getFlight().getDepartureTime())) {
            throw new BaseException(new ErrorMessage(MessageType.TIME_ERROR, "Flight has already departed."));
        }

        ticket.setCheckedIn(true);
        ticketRepository.save(ticket);

        notificationService.sendNotification(ticket.getPassenger(), 
            "Boarding pass ready! You are checked in for flight " + ticket.getFlight().getFlightNumber(), 
            "CHECK_IN");
    }

    @Override
    @Transactional
    public void cancelTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Ticket not found")));

        if (!securityService.hasRole(UserType.ADMIN) && !securityService.isCurrentUser(ticket.getPassenger())) {
            throw new BaseException(new ErrorMessage(MessageType.ACCESS_DENIED, "You can only cancel your own tickets!"));
        }

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Ticket is already cancelled"));
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        Flight flight = ticket.getFlight();
        flight.decrementOccupancy();

        // Refund to SkyWallet
        User passenger = ticket.getPassenger();
        passenger.refundBalance(ticket.getPrice());
        
        // Reverse Miles (10% of price)
        int milesToReverse = ticket.getPrice().divide(new BigDecimal("10"), 0, java.math.RoundingMode.FLOOR).intValue();
        passenger.reverseMiles(milesToReverse);

        userRepository.save(passenger);
        flightRepository.save(flight);
        ticketRepository.save(ticket);

        notificationService.sendNotification(passenger, 
            "Ticket cancelled for flight " + ticket.getFlight().getFlightNumber() + ". Your SkyWallet has been credited (if applicable).", 
            "FLIGHT_CANCELLED");
    }

    @Override
    public List<TicketDto.Info> getMyTickets() {
        User currentUser = securityService.getCurrentUser();
        return ticketRepository.findByPassengerId(currentUser.getId()).stream()
                .map(this::convertToDto).toList();
    }

    private TicketDto.Info convertToDto(Ticket ticket) {
        return TicketDto.Info.builder()
                .id(ticket.getId())
                .price(ticket.getPrice())
                .flightNumber(ticket.getFlight().getFlightNumber())
                .passengerName(ticket.getPassenger().getFirstName() + " " + ticket.getPassenger().getLastName())
                .departureCity(ticket.getFlight().getDepartureAirport().getCity())
                .arrivalCity(ticket.getFlight().getArrivalAirport().getCity())
                .departureTime(ticket.getFlight().getDepartureTime().toString())
                .arrivalTime(ticket.getFlight().getArrivalTime().toString())
                .seatNumber(ticket.getSeatNumber())
                .pnrCode(ticket.getPnrCode())
                .isCheckedIn(ticket.isCheckedIn())
                .status(ticket.getStatus())
                .build();
    }
}