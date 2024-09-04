package org.modsen.service.driver.repository;

import org.modsen.service.driver.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
