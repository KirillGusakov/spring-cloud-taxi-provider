package org.modsen.servicepassenger.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.servicepassenger.model.Passenger;
import org.modsen.servicepassenger.repository.PassengerRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PassengerRepositoryTest {

    @MockBean
    private PassengerRepository passengerRepository;

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passenger = Passenger.builder()
                .id(1L)
                .firstName("Kirill")
                .lastName("Ivanov")
                .email("kirill.ivanov@example.com")
                .phoneNumber("1234567890")
                .isDeleted(false)
                .build();
    }

    @Test
    void testSavePassenger() {
        when(passengerRepository.save(passenger)).thenReturn(passenger);

        Passenger savedPassenger = passengerRepository.save(passenger);

        assertThat(savedPassenger).isNotNull();
        assertThat(savedPassenger.getId()).isEqualTo(1L);
        assertThat(savedPassenger.getEmail()).isEqualTo("kirill.ivanov@example.com");
        verify(passengerRepository, times(1)).save(passenger);
    }

    @Test
    void testFindByIdAndIsDeletedFalse() {
        when(passengerRepository.findByIdAndIsDeletedFalse(passenger.getId()))
                .thenReturn(Optional.of(passenger));

        Optional<Passenger> foundPassenger = passengerRepository.findByIdAndIsDeletedFalse(passenger.getId());

        assertThat(foundPassenger).isPresent();
        assertThat(foundPassenger.get().getEmail()).isEqualTo("kirill.ivanov@example.com");
        verify(passengerRepository, times(1)).findByIdAndIsDeletedFalse(passenger.getId());
    }

    @Test
    void testFindByIdAndIsDeletedFalse_NotFound() {
        when(passengerRepository.findByIdAndIsDeletedFalse(anyLong()))
                .thenReturn(Optional.empty());

        Optional<Passenger> foundPassenger = passengerRepository.findByIdAndIsDeletedFalse(passenger.getId());

        assertThat(foundPassenger).isNotPresent();
        verify(passengerRepository, times(1)).findByIdAndIsDeletedFalse(passenger.getId());
    }

    @Test
    void testUpdatePassenger() {
        when(passengerRepository.save(passenger)).thenReturn(passenger);

        passenger.setLastName("Petrov");
        Passenger updatedPassenger = passengerRepository.save(passenger);

        assertThat(updatedPassenger.getLastName()).isEqualTo("Petrov");
        verify(passengerRepository, times(1)).save(passenger);
    }

    @Test
    void testDeletePassenger() {
        passenger.setIsDeleted(true);
        passengerRepository.save(passenger);

        Optional<Passenger> foundPassenger = passengerRepository.findByIdAndIsDeletedFalse(passenger.getId());
        assertThat(foundPassenger).isNotPresent();
    }



    @Test
    void testExistsByEmailAndIdNot() {
        when(passengerRepository.existsByEmailAndIdNot(anyString(), anyLong()))
                .thenReturn(false);

        boolean exists = passengerRepository.existsByEmailAndIdNot("kirill.ivanov@example.com", passenger.getId());
        assertThat(exists).isFalse();

        when(passengerRepository.existsByEmailAndIdNot("kirill.ivanov@example.com", passenger.getId()))
                .thenReturn(true);

        exists = passengerRepository.existsByEmailAndIdNot("kirill.ivanov@example.com", passenger.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByPhoneNumberAndIdNot() {
        when(passengerRepository.existsByPhoneNumberAndIdNot(anyString(), anyLong()))
                .thenReturn(false);

        boolean exists = passengerRepository.existsByPhoneNumberAndIdNot("1234567890", passenger.getId());
        assertThat(exists).isFalse();

        when(passengerRepository.existsByPhoneNumberAndIdNot("1234567890", passenger.getId()))
                .thenReturn(true);

        exists = passengerRepository.existsByPhoneNumberAndIdNot("1234567890", passenger.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void testFindAll() {
        Passenger passenger2 = Passenger.builder()
                .id(2L)
                .firstName("Alina")
                .lastName("Us")
                .email("alina.us@example.com")
                .phoneNumber("80299090909")
                .isDeleted(false)
                .build();

        when(passengerRepository.findAll()).thenReturn(Arrays.asList(passenger, passenger2));

        List<Passenger> passengers = passengerRepository.findAll();

        assertThat(passengers).isNotNull();
        assertThat(passengers.size()).isEqualTo(2);
        assertThat(passengers.get(0).getFirstName()).isEqualTo("Kirill");
        assertThat(passengers.get(1).getFirstName()).isEqualTo("Alina");
        verify(passengerRepository, times(1)).findAll();
    }
}