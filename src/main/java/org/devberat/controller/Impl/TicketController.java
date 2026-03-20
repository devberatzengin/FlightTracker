package org.devberat.controller.Impl;

import lombok.RequiredArgsConstructor;
import org.devberat.DTO.SeatMapDto;
import org.devberat.DTO.TicketDto;
import org.devberat.model.RootEntity;
import org.devberat.service.ITicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rest/api/ticket")
@RequiredArgsConstructor
public class TicketController extends RestBaseController {

    private final ITicketService ticketService;

    @PostMapping("/book")
    public RootEntity<TicketDto.Info> book(@RequestBody TicketDto.BookingRequest request) {
        return ok(ticketService.bookTicket(request));
    }

    @GetMapping("/my-tickets")
    public RootEntity<List<TicketDto.Info>> getMyTickets() {
        return ok(ticketService.getMyTickets());
    }

    @PostMapping("/check-in/{pnrCode}")
    public RootEntity<String> checkIn(@PathVariable String pnrCode) {
        ticketService.checkIn(pnrCode);
        return ok("Check-in successful! Have a nice flight.");
    }

    @GetMapping("/flight/{flightId}/seats")
    public RootEntity<List<SeatMapDto.SeatInfo>> getSeatMap(@PathVariable UUID flightId) {
        return ok(ticketService.getSeatMap(flightId)); //
    }

    @PutMapping("/cancel/{id}")
    public RootEntity<Void> cancel(@PathVariable UUID id) {
        ticketService.cancelTicket(id);
        return ok(null);
    }
}