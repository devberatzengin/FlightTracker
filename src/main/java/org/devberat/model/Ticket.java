package org.devberat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @Column(name = "pnr_code", unique = true, nullable = false)
    private String pnrCode;

    @Column(name = "is_checked_in")
    private boolean isCheckedIn = false;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "price")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private Date createdAt;
}