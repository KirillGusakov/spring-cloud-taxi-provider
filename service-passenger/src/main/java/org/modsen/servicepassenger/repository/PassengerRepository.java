package org.modsen.servicepassenger.repository;

import org.modsen.servicepassenger.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByIdAndIsDeletedFalse(Long id);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phone);
}