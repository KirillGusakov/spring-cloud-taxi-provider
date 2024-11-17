package org.modsen.serviceride.unit.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.serviceride.dto.filter.RideFilterDto;
import org.modsen.serviceride.dto.message.RatingMessage;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.request.RideUpdateRequest;
import org.modsen.serviceride.dto.response.DriverResponse;
import org.modsen.serviceride.dto.response.PageResponse;
import org.modsen.serviceride.dto.response.PassengerResponse;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.mapper.RideMapper;
import org.modsen.serviceride.model.Ride;
import org.modsen.serviceride.model.RideStatus;
import org.modsen.serviceride.repository.RideRepository;
import org.modsen.serviceride.service.impl.RideServiceImpl;
import org.modsen.serviceride.util.DoRequestUtil;
import org.modsen.serviceride.util.RideTestUtil;
import org.modsen.serviceride.util.RideUtil;
import org.modsen.serviceride.util.SecurityTestUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RideServiceUnitTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RideMapper rideMapper;

    @Mock
    private RideUtil rideUtil;

    @Mock
    private KafkaTemplate<String, RatingMessage> kafkaTemplate;

    @Mock
    private DoRequestUtil doRequestUtil;

    @InjectMocks
    private RideServiceImpl rideService;

    private Ride ride;
    private RideResponse rideResponse;
    private RideRequest rideRequest;
    private RideUpdateRequest updateRequest;
    private DriverResponse driverResponse;
    private PassengerResponse passengerResponse;

    @BeforeEach
    void setUp() {
        ride = RideTestUtil.ride;
        rideResponse = RideTestUtil.rideResponse;
        rideRequest = RideTestUtil.rideRequest;
        updateRequest = RideTestUtil.updateRequest;
        driverResponse = RideTestUtil.driverResponse;
        passengerResponse = RideTestUtil.passengerResponse;
    }

    @Test
    void givenRideExists_whenFindById_thenReturnRideResponse() {
        // Given
        SecurityTestUtils.setUpSecurityContextWithRole("ROLE_USER");
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
        Pageable pageable = PageRequest.of(0, 10);
        RideFilterDto filterDto = new RideFilterDto();
        filterDto.setDriverId(1L);
        filterDto.setPassengerId(2L);
        filterDto.setDestinationAddress("Test Destination");
        filterDto.setPickupAddress("Test Pickup");
        filterDto.setStatus("ACCEPTED");

        rideResponse = new RideResponse();
        rideResponse.setId(1L);
        rideResponse.setDriverId(1L);
        rideResponse.setPassengerId(2L);
        rideResponse.setDestinationAddress("Test Destination");
        rideResponse.setPickupAddress("Test Pickup");
        rideResponse.setStatus(RideStatus.ACCEPTED.toString());

        Ride ride = new Ride();
        ride.setDriverId(1L);
        ride.setPassengerId(2L);
        ride.setDestinationAddress("Test Destination");
        ride.setPickupAddress("Test Pickup");
        ride.setStatus(RideStatus.ACCEPTED);

        when(rideUtil.createRideExample(filterDto)).thenReturn(Example.of(ride));
        Page<Ride> ridePage = new PageImpl<>(Collections.singletonList(ride), pageable, 1);
        when(rideRepository.findAll(Example.of(ride), pageable)).thenReturn(ridePage);
        when(rideMapper.toRideResponse(ride)).thenReturn(rideResponse);

        Map<String, Object> result = rideService.findAll(pageable, filterDto);

        assertNotNull(result);
        assertTrue(result.containsKey("rides"));
        assertTrue(result.containsKey("pageInfo"));
        assertEquals(1, ((PageResponse) result.get("pageInfo")).getTotalItems());
        assertEquals(1, ((PageResponse) result.get("pageInfo")).getTotalPages());
        assertEquals(rideResponse, ((List<RideResponse>) result.get("rides")).get(0));
    }

    @Test
    void givenRideRequest_whenSaveRide_thenReturnSavedRideResponse() {
        // Given
        SecurityTestUtils.setUpSecurityContextWithRole("ROLE_USER");
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
        SecurityTestUtils.setUpSecurityContextWithRole("ROLE_USER");
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideMapper.toRideResponse(ride)).thenReturn(rideResponse);

        // When
        RideResponse updatedRide = rideService.update(1L, updateRequest);

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
        SecurityTestUtils.setUpSecurityContextWithRole("ROLE_USER");
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