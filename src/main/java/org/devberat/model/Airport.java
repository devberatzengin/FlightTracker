package org.devberat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;

import java.util.UUID;

@Entity
@Data
@Table(name = "airport")
@AllArgsConstructor
@NoArgsConstructor
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "iata_code", updatable = false, nullable = false, unique = true)
    private String iataCode;

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    @Column(name = "city", updatable = false, nullable = false)
    private String city;

    @Column(name = "country", updatable = false, nullable = false)
    private String country;

    @Column(name = "is_active", updatable = true, nullable = false)
    private boolean isActive;



}
