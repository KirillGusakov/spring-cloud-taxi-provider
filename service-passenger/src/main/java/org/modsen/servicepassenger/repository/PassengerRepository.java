package org.modsen.servicepassenger.repository;

import org.modsen.servicepassenger.model.Passenger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByIdAndIsDeletedFalse(Long id);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phone);

    @Query("SELECT p FROM Passenger p WHERE "
            + "(:email IS NULL OR p.email LIKE %:email%) AND "
            + "(:name IS NULL OR p.firstName LIKE %:name%) AND "
            + "(:phone IS NULL OR p.phoneNumber LIKE %:phone%) AND "
            + "(:isDeleted IS NULL OR p.isDeleted = :isDeleted)")
    List<Passenger> findAllWithFilters(@Param("email") String email,
                                       @Param("name") String name,
                                       @Param("phone") String phone,
                                       @Param("isDeleted") Boolean isDeleted,
                                       Pageable pageable);
}