package org.devberat.repository;

import org.devberat.model.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAircraftRepository extends JpaRepository<Aircraft, UUID> {
    Optional<Aircraft> findBySerialNumber(String serialNumber);
}