package org.modsen.servicepassenger.unit.service.impl;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerServiceImpl passengerService;
    private Passenger passenger;
    private PassengerResponseDto passengerResponseDto;


    @Test
    @BeforeEach
    void setUp() {
        passenger = Passenger.builder()
                .id(1L)
                .email("kirill.kirill@example.com")
                .firstName("Kirill")
                .lastName("Kirill")
                .phoneNumber("+37544596912")
                .isDeleted(false)
                .build();

        passengerResponseDto = new PassengerResponseDto(
                1L, "Kirill", "Kirill",
                "kirill.kirill@example.com", "+37544596912", false);
    }

    @Test
    void findById_success() {
        when(passengerRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(passenger));
        when(passengerMapper.toPassengerResponseDto(passenger)).thenReturn(passengerResponseDto);

        PassengerResponseDto foundPassenger = passengerService.findById(1L);

        assertNotNull(foundPassenger);
        assertEquals(passengerResponseDto, foundPassenger);
        verify(passengerRepository, times(1)).findByIdAndIsDeletedFalse(1L);
    }

    @Test
    void findById_notFound() {
        when(passengerRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> passengerService.findById(1L));
    }

    @Test
    void findAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Passenger passengerExample = Passenger.builder().email("kirill.kirill@example.com")
                .firstName("Kirill")
                .build();
        Page<Passenger> passengers = new PageImpl<>(Collections.singletonList(passenger));

        when(passengerRepository.findAll(any(Example.class), eq(pageable))).thenReturn(passengers);
        when(passengerMapper.toPassengerResponseDto(passenger)).thenReturn(passengerResponseDto);

        Page<PassengerResponseDto> result =
                passengerService.findAll(pageable, "kirill.kirill@example.com", "Kirill", null, false);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(passengerRepository, times(1)).findAll(any(Example.class), eq(pageable));
    }

    @Test
    void save_success() {
        PassengerRequestDto passengerRequestDto = new PassengerRequestDto("Kirill", "Kirill",
                "kirill.kirill@example.com", "+37544596912");
        when(passengerRepository.existsByEmailAndIdNot("kirill.kirill@example.com", 0L)).thenReturn(false);
        when(passengerRepository.existsByPhoneNumberAndIdNot("+37544596912", 0L)).thenReturn(false);
        when(passengerMapper.toPassenger(passengerRequestDto)).thenReturn(passenger);
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        when(passengerMapper.toPassengerResponseDto(passenger)).thenReturn(passengerResponseDto);

        PassengerResponseDto result = passengerService.save(passengerRequestDto);

        assertNotNull(result);
        assertEquals(passengerResponseDto, result);
        verify(passengerRepository, times(1)).save(passenger);
    }

    @Test
    void update_success() {
        PassengerRequestDto passengerRequestDto = new PassengerRequestDto
                ("Kirill", "Kirill", "kirill.kirill@example.com", "+37544596912");

        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(passengerRepository.existsByEmailAndIdNot("kirill.kirill@example.com", passenger.getId())).thenReturn(false);
        when(passengerRepository.existsByPhoneNumberAndIdNot("+37544596912", passenger.getId())).thenReturn(false);
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        when(passengerMapper.toPassengerResponseDto(passenger)).thenReturn(passengerResponseDto);

        PassengerResponseDto result = passengerService.update(1L, passengerRequestDto);

        assertNotNull(result);
        assertEquals(passengerResponseDto, result);
        verify(passengerRepository, times(1)).save(passenger);
    }

    @Test
    void delete_success() {
        when(passengerRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(passenger));

        passengerService.delete(1L);

        verify(passengerRepository, times(1)).save(passenger);
        assertTrue(passenger.getIsDeleted());
    }

}