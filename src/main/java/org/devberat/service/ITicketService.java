package org.devberat.service;

import org.devberat.DTO.TicketDto;
import java.util.List;
import java.util.UUID;

public interface ITicketService {
    TicketDto.Info bookTicket(TicketDto.BookingRequest request);
    void cancelTicket(UUID ticketId);
    List<TicketDto.Info> getMyTickets();

    public void checkIn(String pnrCode);
}