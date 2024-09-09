package org.modsen.service.driver.repository;

import org.modsen.service.driver.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByNumber(String number);
    Page<Car> findByModelContainingIgnoreCaseAndNumberContainingIgnoreCase(String model, String number, Pageable pageable);
}