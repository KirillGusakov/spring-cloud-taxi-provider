package org.modsen.serviceride.unit.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.serviceride.client.DriverClient;
import org.modsen.serviceride.client.PassengerClient;
import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.message.RatingMessage;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.response.DriverResponse;
import org.modsen.serviceride.dto.response.PassengerResponse;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.mapper.RideMapper;
import org.modsen.serviceride.model.Ride;
import org.modsen.serviceride.model.RideStatus;
import org.modsen.serviceride.repository.RideRepository;
import org.modsen.serviceride.service.impl.RideServiceImpl;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceUnitTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RideMapper rideMapper;

    @Mock
    private DriverClient driverClient;

    @Mock
    private PassengerClient passengerClient;

    @Mock
    private KafkaTemplate<String, RatingMessage> kafkaTemplate;

    @InjectMocks
    private RideServiceImpl rideService;

    private Ride ride;
    private RideResponse rideResponse;
    private RideRequest rideRequest;
    private DriverResponse driverResponse;
    private PassengerResponse passengerResponse;

    @BeforeEach
    void setUp() {
        ride = Ride.builder()
                .id(1L)
                .driverId(2L)
                .passengerId(3L)
                .pickupAddress("Start")
                .destinationAddress("End")
                .orderTime(LocalDateTime.now())
                .status(RideStatus.CREATED)
                .price(BigDecimal.valueOf(100))
                .build();

        rideResponse = RideResponse.builder()
                .id(1L)
                .driverId(2L)
                .passengerId(3L)
                .pickupAddress("Start")
                .destinationAddress("End")
                .status(RideStatus.CREATED.name())
                .orderTime(ride.getOrderTime())
                .price(ride.getPrice())
                .build();

        rideRequest = RideRequest.builder()
                .driverId(2L)
                .passengerId(3L)
                .pickupAddress("Start")
                .destinationAddress("End")
                .status(RideStatus.CREATED.name())
                .price(BigDecimal.valueOf(100))
                .build();

        driverResponse = DriverResponse.builder()
                .id(2L)
                .name("Driver Name")
                .build();

        passengerResponse = PassengerResponse.builder()
                .id(3L)
                .firstName("Passenger")
                .lastName("Name")
                .build();
    }

    @Test
    void givenRideExists_whenFindById_thenReturnRideResponse() {
        // Given
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideMapper.toRideResponse(ride)).thenReturn(rideResponse);

        // When
        RideResponse foundRide = rideService.findById(1L);

        // Then
        assertNotNull(foundRide);
        assertEquals(rideResponse, foundRide);
        verify(rideRepository, times(1)).findById(1L);
    }

    @Test
    void givenRideDoesNotExist_whenFindById_thenThrowNoSuchElementException() {
        // Given
        when(rideRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> rideService.findById(1L));
        verify(rideRepository, times(1)).findById(1L);
    }

    @Test
    void givenRidesExist_whenFindAll_thenReturnRideResponsePage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        RideFilterDto filterDto = new RideFilterDto();
        Page<Ride> rides = new PageImpl<>(Collections.singletonList(ride));

        when(rideRepository.findAll(any(Example.class), eq(pageable))).thenReturn(rides);
        when(rideMapper.toRideResponse(ride)).thenReturn(rideResponse);

        // When
        Page<RideResponse> result = rideService.findAll(pageable, filterDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(rideResponse, result.getContent().get(0));
        verify(rideRepository, times(1)).findAll(any(Example.class), eq(pageable));
    }

    @Test
    void givenRideRequest_whenSaveRide_thenReturnSavedRideResponse() {
        // Given
        when(driverClient.getDriver(rideRequest.getDriverId())).thenReturn(driverResponse);
        when(passengerClient.getPassenger(rideRequest.getPassengerId())).thenReturn(passengerResponse);
        when(rideMapper.toRide(rideRequest)).thenReturn(ride);
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);
        when(rideMapper.toRideResponse(ride)).thenReturn(rideResponse);

        // When
        RideResponse savedRide = rideService.save(rideRequest);

        // Then
        assertNotNull(savedRide);
        assertEquals(rideResponse, savedRide);
        verify(rideRepository, times(1)).save(ride);
        verify(kafkaTemplate, times(1)).send(eq("rating-topic"), any(RatingMessage.class));
    }

    @Test
    void givenRideExists_whenUpdateRide_thenReturnUpdatedRideResponse() {
        // Given
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(driverClient.getDriver(rideRequest.getDriverId())).thenReturn(driverResponse);
        when(passengerClient.getPassenger(rideRequest.getPassengerId())).thenReturn(passengerResponse);
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideMapper.toRideResponse(ride)).thenReturn(rideResponse);

        // When
        RideResponse updatedRide = rideService.update(1L, rideRequest);

        // Then
        assertNotNull(updatedRide);
        assertEquals(rideResponse, updatedRide);
        verify(rideRepository, times(1)).save(ride);
    }

    @Test
    void givenRideExists_whenDeleteRide_thenRideShouldBeDeleted() {
        // Given
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        // When
        rideService.delete(1L);

        // Then
        verify(rideRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenRideExists_whenUpdateRideStatus_thenReturnUpdatedRideResponse() {
        // Given
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideMapper.toRideResponse(ride)).thenReturn(rideResponse);

        // When
        RideResponse updatedRide = rideService.updateRideStatus(1L, "COMPLETED");

        // Then
        assertNotNull(updatedRide);
        assertEquals(RideStatus.COMPLETED.name(), ride.getStatus().name());
        verify(rideRepository, times(1)).save(ride);
    }
}