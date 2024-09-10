package org.modsen.servicerating.repository;

import org.modsen.servicerating.model.Rating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.driverId = :driverId")
    Optional<Double> findAverageRatingByDriverId(@Param("driverId") Long driverId);

    @Query("SELECT r FROM Rating r " +
            "WHERE (:driverId IS NULL OR r.driverId = :driverId) " +
            "AND (:userId IS NULL OR r.userId = :userId) " +
            "AND (:rating IS NULL OR r.rating = :rating)")
    List<Rating> findByFilter(@Param("driverId") Long driverId,
                              @Param("userId") Long userId,
                              @Param("rating") Integer rating,
                              Pageable pageable);
}
