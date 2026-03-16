package org.devberat.repository;

import org.devberat.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface IFlightRepository extends JpaRepository<Flight, UUID> {
    java.util.Optional<Flight> findByFlightNumber(String flightNumber);

    @Query("SELECT COUNT(f) > 0 FROM Flight f WHERE f.aircraft.serialNumber = :serialNumber " +
            "AND (:departureTime < f.arrivalTime AND :arrivalTime > f.departureTime)")
    boolean isAircraftBusy(@    Param("serialNumber") String serialNumber,
                           @Param("departureTime") LocalDateTime departureTime,
                           @Param("arrivalTime") LocalDateTime arrivalTime);

}