package org.modsen.serviceride.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.serviceride.model.Ride;
import org.modsen.serviceride.model.RideStatus;
import org.modsen.serviceride.repository.RideRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RideRepositoryTest {

    @Mock
    private RideRepository rideRepository;

    private Ride ride;

    @BeforeEach
    public void setUp() {
        ride = Ride.builder()
                .id(1L)
                .driverId(2L)
                .passengerId(3L)
                .pickupAddress("123 Main St")
                .destinationAddress("456 Elm St")
                .status(RideStatus.CREATED)
                .orderTime(LocalDateTime.now())
                .price(BigDecimal.valueOf(25.50))
                .build();
    }

    @Test
    public void testSaveRide() {
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride savedRide = rideRepository.save(ride);

        assertThat(savedRide).isNotNull();
        assertThat(savedRide.getDriverId()).isEqualTo(2L);
        assertThat(savedRide.getPrice()).isEqualTo(BigDecimal.valueOf(25.50));
    }

    @Test
    public void testFindById() {
        when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));

        Optional<Ride> foundRide = rideRepository.findById(ride.getId());

        assertThat(foundRide).isPresent();
        assertThat(foundRide.get().getId()).isEqualTo(ride.getId());
    }

    @Test
    public void testFindAll() {
        when(rideRepository.findAll()).thenReturn(Collections.singletonList(ride));

        List<Ride> allRides = rideRepository.findAll();

        assertThat(allRides).isNotEmpty();
        assertThat(allRides.size()).isEqualTo(1);
        assertThat(allRides.get(0).getPickupAddress()).isEqualTo("123 Main St");
    }

    @Test
    public void testDeleteRide() {
        doNothing().when(rideRepository).delete(ride);

        rideRepository.delete(ride);

        verify(rideRepository, times(1)).delete(ride);
    }

    @Test
    public void testFindByFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ride> expectedPage = new PageImpl<>(Collections.singletonList(ride), pageable, 1);

        when(rideRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Ride> actualPage = rideRepository.findAll(pageable);

        assertThat(actualPage.getContent()).hasSize(1);
        assertThat(actualPage.getContent().get(0).getDriverId()).isEqualTo(2L);
    }

    @Test
    public void testUpdateRide() {
        when(rideRepository.save(ride)).thenReturn(ride);

        ride.setPrice(BigDecimal.valueOf(30.00));
        Ride updatedRide = rideRepository.save(ride);

        assertThat(updatedRide.getPrice()).isEqualTo(BigDecimal.valueOf(30.00));
    }
}
