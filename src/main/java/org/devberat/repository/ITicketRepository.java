package org.devberat.repository;

import org.devberat.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByPassengerId(UUID passengerId);
    boolean existsByFlightIdAndSeatNumberAndStatus(UUID flightId, String seatNumber, org.devberat.model.TicketStatus status);

    Optional<Ticket> findByPnrCode(String pnrCode);

}