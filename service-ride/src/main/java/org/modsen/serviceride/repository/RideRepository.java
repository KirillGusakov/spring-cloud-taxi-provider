package org.modsen.serviceride.repository;

import org.modsen.serviceride.model.Ride;
import org.modsen.serviceride.model.RideStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    @Query("SELECT r FROM Ride r WHERE "
            + "(:driverId IS NULL OR r.driverId = :driverId) AND "
            + "(:passengerId IS NULL OR r.passengerId = :passengerId) AND "
            + "(:pickupAddress IS NULL OR r.pickupAddress LIKE %:pickupAddress%) AND "
            + "(:destinationAddress IS NULL OR r.destinationAddress LIKE %:destinationAddress%) AND "
            + "(:status IS NULL OR r.status = :status) AND "
            + "(:minPrice IS NULL OR r.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR r.price <= :maxPrice)")
    List<Ride> findAllWithFilters(
            @Param("driverId") Long driverId,
            @Param("passengerId") Long passengerId,
            @Param("pickupAddress") String pickupAddress,
            @Param("destinationAddress") String destinationAddress,
            @Param("status") RideStatus status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}