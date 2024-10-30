package org.modsen.servicepassenger.repository;

import org.modsen.servicepassenger.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByIdAndIsDeletedFalse(Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneNumberAndIdNot(String phone, Long id);

    boolean existsBySub(UUID sub);
}