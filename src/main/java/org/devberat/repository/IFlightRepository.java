package org.devberat.repository;

import org.devberat.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface IFlightRepository extends JpaRepository<Flight, UUID> {
    java.util.Optional<Flight> findByFlightNumber(String flightNumber);

}