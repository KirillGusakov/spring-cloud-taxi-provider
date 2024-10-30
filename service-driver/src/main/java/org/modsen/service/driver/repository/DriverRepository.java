package org.modsen.service.driver.repository;

import org.modsen.service.driver.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByPhoneNumberAndIdNot(String number, Long id);
    Page<Driver> findByNameContainingIgnoreCaseAndPhoneNumberContaining(String name, String phone, Pageable pageable);
    boolean existsByUuid(UUID uuid);
}