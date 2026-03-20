package org.devberat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "flight")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;

    @Column(name = "current_occupancy")
    private Integer currentOccupancy = 0;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @ManyToOne
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arrivalAirport;

    @ManyToOne
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private User captain;

    @ManyToOne
    @JoinColumn(name = "tower_officer_id")
    private User towerOfficer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FlightStatus status;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    // Business Logic Methods

    public void incrementOccupancy() {
        if (this.currentOccupancy == null) this.currentOccupancy = 0;
        if (isFull()) {
            throw new RuntimeException("Flight is already full");
        }
        this.currentOccupancy++;
    }

    public void decrementOccupancy() {
        if (this.currentOccupancy == null || this.currentOccupancy <= 0) {
            this.currentOccupancy = 0;
            return;
        }
        this.currentOccupancy--;
    }

    public boolean isFull() {
        if (this.aircraft == null) return false;
        return this.currentOccupancy >= this.aircraft.getSeatCapacity();
    }
}