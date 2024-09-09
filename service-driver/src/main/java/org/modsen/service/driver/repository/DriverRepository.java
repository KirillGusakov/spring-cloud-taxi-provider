package org.modsen.service.driver.repository;

import org.modsen.service.driver.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByPhoneNumber(String number);
    Page<Driver> findByNameContainingIgnoreCaseAndPhoneNumberContaining(String name, String phone, Pageable pageable);
}