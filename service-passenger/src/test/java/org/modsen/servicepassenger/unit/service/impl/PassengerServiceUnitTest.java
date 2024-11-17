package org.modsen.servicepassenger.unit.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.mapper.PassengerMapper;
import org.modsen.servicepassenger.model.Passenger;
import org.modsen.servicepassenger.repository.PassengerRepository;
import org.modsen.servicepassenger.service.impl.PassengerServiceImpl;
import org.modsen.servicepassenger.util.PassengerTestUtil;
import org.modsen.servicepassenger.util.PassengerUtil;
import org.modsen.servicepassenger.util.SecurityTestUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Passenger service unit tests")
public class PassengerServiceUnitTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;
    @Mock
    private PassengerUtil passengerUtil;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private Passenger passenger;
    private PassengerResponseDto passengerResponseDto;

    @BeforeEach
    void setUp() {
        passenger = PassengerTestUtil.passenger;
        passengerResponseDto = PassengerTestUtil.responseDto;
    }

    @Test
    void givenExistingPassengerId_whenFindById_thenReturnPassenger() {
        // Given
        SecurityTestUtil.setUpSecurityContextWithRole("ROLE_USER");
        when(passengerRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(passenger));
        when(passengerMapper.toPassengerResponseDto(passenger)).thenReturn(passengerResponseDto);

        // When
        PassengerResponseDto foundPassenger = passengerService.findById(1L, passenger.getSub().toString());

        // Then
        assertNotNull(foundPassenger);
        assertEquals(passengerResponseDto, foundPassenger);
        verify(passengerRepository, times(1)).findByIdAndIsDeletedFalse(1L);
    }

    @Test
    void givenNonExistingPassengerId_whenFindById_thenThrowException() {
        // Given
        when(passengerRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> passengerService.findById(1L, passenger.getSub().toString()));
    }

    @Test
    void givenPassengerSearchCriteria_whenFindAll_thenReturnPassengerPage() {
        // Given
        SecurityTestUtil.setUpSecurityContextWithRole("ROLE_ADMIN");
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Passenger passengerExample = Passenger.builder()
                .email("kirill@gmail.com")
                .firstName("Kirill")
                .phoneNumber(" ")
                .isDeleted(false)
                .build();
        Example<Passenger> exampleExample = Example.of(passengerExample);

        Page<Passenger> passengers = new PageImpl<>(Collections.singletonList(passenger));

        when(passengerUtil.createPassengerExample("kirill@gmail.com", "Kirill", " ", false)).thenReturn(exampleExample);
        when(passengerRepository.findAll(eq(exampleExample), eq(pageable))).thenReturn(passengers);
        when(passengerMapper.toPassengerResponseDto(passenger)).thenReturn(passengerResponseDto);

        // When
        Map<String, Object> result = passengerService.findAll(pageable, "kirill@gmail.com", "Kirill", " ", false);

        // Then
        assertNotNull(result);
        verify(passengerRepository, times(1)).findAll(eq(exampleExample), eq(pageable));
    }



    @Test
    void givenPassengerRequestDto_whenSave_thenReturnSavedPassenger() {
        // Given
        PassengerRequestDto passengerRequestDto = PassengerTestUtil.passengerRequestDto;

        // When
        when(passengerMapper.toPassenger(passengerRequestDto)).thenReturn(passenger);
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        when(passengerMapper.toPassengerResponseDto(passenger)).thenReturn(passengerResponseDto);
        PassengerResponseDto result = passengerService.save(passengerRequestDto, passenger.getSub().toString());

        // Then
        assertNotNull(result);
        assertEquals(passengerResponseDto, result);
        verify(passengerRepository, times(1)).save(passenger);
    }

    @Test
    void givenPassengerRequestDto_whenUpdate_thenReturnUpdatedPassenger() {
        // Given
        SecurityTestUtil.setUpSecurityContextWithRole("ROLE_USER");
        PassengerRequestDto passengerRequestDto = PassengerTestUtil.passengerRequestDto;

        // When
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        when(passengerMapper.toPassengerResponseDto(passenger)).thenReturn(passengerResponseDto);
        PassengerResponseDto result = passengerService.update(1L, passengerRequestDto, passenger.getSub().toString());

        // Then
        assertNotNull(result);
        assertEquals(passengerResponseDto, result);
        verify(passengerRepository, times(1)).save(passenger);
    }

    @Test
    void givenExistingPassengerId_whenDelete_thenMarkAsDeleted() {
        // Given
        SecurityTestUtil.setUpSecurityContextWithRole("ROLE_USER");

        // When
        when(passengerRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(passenger));
        passengerService.delete(1L, passenger.getSub().toString());

        // Then
        verify(passengerRepository, times(1)).save(passenger);
        assertTrue(passenger.getIsDeleted());
    }
}
