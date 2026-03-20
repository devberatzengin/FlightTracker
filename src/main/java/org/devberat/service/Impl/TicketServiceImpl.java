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
import org.devberat.service.ITicketService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements ITicketService {

    private final ITicketRepository ticketRepository;
    private final IFlightRepository flightRepository;

    private String generateUniquePnr() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder pnr = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            pnr.append(characters.charAt(random.nextInt(characters.length())));
        }
        return pnr.toString();
    }

    private java.math.BigDecimal calculateCurrentPrice(Flight flight) {
        double occupancyRate = (double) flight.getCurrentOccupancy() / flight.getAircraft().getSeatCapacity();
        java.math.BigDecimal currentPrice = flight.getBasePrice();

        // Occupancy factor for pricing
        if (occupancyRate > 0.9) {
            currentPrice = currentPrice.multiply(java.math.BigDecimal.valueOf(1.5)); // %90 dolulukta fiyatı %50 artır
        } else if (occupancyRate > 0.7) {
            currentPrice = currentPrice.multiply(java.math.BigDecimal.valueOf(1.3)); // %70 dolulukta fiyatı %30 artır
        } else if (occupancyRate > 0.5) {
            currentPrice = currentPrice.multiply(java.math.BigDecimal.valueOf(1.1)); // %50 dolulukta fiyatı %10 artır
        }

        // Last minute discount
        // Last 24H and aircraft has less than %20 occupancy then make  %20 discount.
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (flight.getDepartureTime().isBefore(now.plusDays(1)) && occupancyRate < 0.2) {
            currentPrice = currentPrice.multiply(java.math.BigDecimal.valueOf(0.8));
        }

        return currentPrice;
    }

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

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Flight not found")));

        if (flight.getCurrentOccupancy() >= flight.getAircraft().getSeatCapacity()) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Flight is full!"));
        }

        if (ticketRepository.existsByFlightIdAndSeatNumberAndStatus(flight.getId(), request.getSeatNumber(), TicketStatus.ACTIVE)) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Seat " + request.getSeatNumber() + " is already taken!"));
        }

        BigDecimal finalPrice = calculateCurrentPrice(flight);

        Ticket ticket = new Ticket();
        ticket.setPnrCode(generateUniquePnr());
        ticket.setCheckedIn(false);
        ticket.setFlight(flight);
        ticket.setPassenger(currentUser);
        ticket.setSeatNumber(request.getSeatNumber());
        ticket.setPrice(finalPrice);
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setCreatedAt(new java.util.Date());

        flight.setCurrentOccupancy(flight.getCurrentOccupancy() + 1);
        flightRepository.save(flight);

        Ticket saved = ticketRepository.save(ticket);
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

        // Checking for last 24 hours.
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(ticket.getFlight().getDepartureTime().minusDays(1))) {
            throw new BaseException(new ErrorMessage(MessageType.TIME_ERROR, "Check-in opens 24 hours before departure."));
        }

        ticket.setCheckedIn(true);
        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void cancelTicket(UUID ticketId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Ticket not found")));

        if (!currentUser.getUserType().equals(UserType.ADMIN) &&
                !ticket.getPassenger().getId().equals(currentUser.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.ACCESS_DENIED, "You can only cancel your own tickets!"));
        }

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Ticket is already cancelled"));
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        Flight flight = ticket.getFlight();
        flight.setCurrentOccupancy(flight.getCurrentOccupancy() - 1);

        flightRepository.save(flight);
        ticketRepository.save(ticket);
    }

    @Override
    public List<TicketDto.Info> getMyTickets() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ticketRepository.findByPassengerId(currentUser.getId()).stream()
                .map(this::convertToDto).toList();
    }

    private TicketDto.Info convertToDto(Ticket ticket) {
        return TicketDto.Info.builder()
                .id(ticket.getId())
                .flightNumber(ticket.getFlight().getFlightNumber())
                .passengerName(ticket.getPassenger().getFirstName() + " " + ticket.getPassenger().getLastName())
                .seatNumber(ticket.getSeatNumber())
                .status(ticket.getStatus())
                .build();
    }
}