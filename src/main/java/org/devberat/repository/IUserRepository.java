package org.devberat.repository;

import org.devberat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);


    @Query("SELECT u FROM User u WHERE u.userType = 'CAPTAIN' AND u.isActive = true " +
            "AND NOT EXISTS (SELECT f FROM Flight f WHERE f.captain = u " +
            "AND (:departureTime < f.arrivalTime AND :arrivalTime > f.departureTime))")
    List<User> findAvailableCaptains(@Param("departureTime") LocalDateTime departureTime,
                                     @Param("arrivalTime") LocalDateTime arrivalTime);

}