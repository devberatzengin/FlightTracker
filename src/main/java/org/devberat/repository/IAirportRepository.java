package org.devberat.repository;

import org.devberat.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAirportRepository extends JpaRepository<Airport, UUID> {
    Optional<Airport> findByIataCode(String iataCode);
}