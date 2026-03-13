package org.devberat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "aircraft")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "model", nullable = false, updatable = false)
    private String model;

    @Column(name = "serial_number", nullable = false, updatable = false, unique = true)
    private String serialNumber;

    @Column(name= "seat_capacity", nullable = false, updatable = true)
    private int seatCapacity;

    @Column(name = "in_service", nullable = false, updatable = true)
    private boolean inService;


}
