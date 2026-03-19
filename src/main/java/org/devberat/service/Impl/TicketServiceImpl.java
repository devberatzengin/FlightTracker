package org.devberat.service.Impl;

import lombok.RequiredArgsConstructor;
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

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements ITicketService {

    private final ITicketRepository ticketRepository;
    private final IFlightRepository flightRepository;

    @Override
    @Transactional
    public TicketDto.Info bookTicket(TicketDto.BookingRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Flight not found")));

        // Check availability
        if (flight.getCurrentOccupancy() >= flight.getAircraft().getSeatCapacity()) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Flight is full!"));
        }

        // Check for booking
        if (ticketRepository.existsByFlightIdAndSeatNumberAndStatus(flight.getId(), request.getSeatNumber(), TicketStatus.ACTIVE)) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Seat " + request.getSeatNumber() + " is already taken!"));
        }

        // Create ticket
        Ticket ticket = new Ticket();
        ticket.setFlight(flight);
        ticket.setPassenger(currentUser);
        ticket.setSeatNumber(request.getSeatNumber());
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setCreatedAt(new Date());

        // Update occupancy
        flight.setCurrentOccupancy(flight.getCurrentOccupancy() + 1);
        flightRepository.save(flight);

        Ticket saved = ticketRepository.save(ticket);
        return convertToDto(saved);
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