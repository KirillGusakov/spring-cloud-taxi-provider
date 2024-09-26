package org.modsen.service.driver.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.service.driver.model.Driver;
import org.modsen.service.driver.model.Sex;
import org.modsen.service.driver.repository.DriverRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DriverRepositoryTest {

    @Mock
    private DriverRepository driverRepository;

    private Driver testDriver;

    @BeforeEach
    void setUp() {
        testDriver = Driver.builder()
                .id(1L)
                .name("Kirill")
                .phoneNumber("1234567890")
                .sex(Sex.M)
                .build();
    }

    @Test
    void saveDriver_success() {
        when(driverRepository.save(testDriver)).thenReturn(testDriver);

        Driver savedDriver = driverRepository.save(testDriver);

        assertNotNull(savedDriver);
        assertEquals(testDriver.getId(), savedDriver.getId());
        assertEquals(testDriver.getName(), savedDriver.getName());
        assertEquals(testDriver.getPhoneNumber(), savedDriver.getPhoneNumber());
        assertEquals(testDriver.getSex(), savedDriver.getSex());
        verify(driverRepository, times(1)).save(testDriver);
    }

    @Test
    void findById_returnsDriver_whenDriverExists() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(testDriver));

        Optional<Driver> foundDriver = driverRepository.findById(1L);

        assertTrue(foundDriver.isPresent());
        assertEquals(testDriver, foundDriver.get());
        verify(driverRepository, times(1)).findById(1L);
    }

    @Test
    void findById_returnsEmpty_whenDriverDoesNotExist() {
        when(driverRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Driver> foundDriver = driverRepository.findById(2L);

        assertFalse(foundDriver.isPresent());
        verify(driverRepository, times(1)).findById(2L);
    }

    @Test
    void updateDriver_success() {
        when(driverRepository.save(testDriver)).thenReturn(testDriver);

        Driver driverToUpdate = testDriver;
        driverToUpdate.setName("Alina");

        Driver updatedDriver = driverRepository.save(driverToUpdate);

        assertNotNull(updatedDriver);
        assertEquals("Alina", updatedDriver.getName());
        assertEquals(testDriver.getPhoneNumber(), updatedDriver.getPhoneNumber());
        assertEquals(testDriver.getSex(), updatedDriver.getSex());
        verify(driverRepository, times(1)).save(driverToUpdate);
    }

    @Test
    void deleteDriver_success() {
        driverRepository.deleteById(1L);
        verify(driverRepository, times(1)).deleteById(1L);
    }

    @Test
    void testExistsByPhoneNumberAndIdNot_ReturnsTrue_WhenDriverExists() {
        when(driverRepository.existsByPhoneNumberAndIdNot("1234567890", 0L)).thenReturn(true);

        boolean exists = driverRepository.existsByPhoneNumberAndIdNot("1234567890", 0L);

        assertTrue(exists);
        verify(driverRepository, times(1)).existsByPhoneNumberAndIdNot("1234567890", 0L);
    }

    @Test
    void testExistsByPhoneNumberAndIdNot_ReturnsFalse_WhenDriverDoesNotExist() {
        when(driverRepository.existsByPhoneNumberAndIdNot("0987654321", 1L)).thenReturn(false);

        boolean exists = driverRepository.existsByPhoneNumberAndIdNot("0987654321", 1L);

        assertFalse(exists);
        verify(driverRepository, times(1)).existsByPhoneNumberAndIdNot("0987654321", 1L);
    }

    @Test
    void testFindByNameContainingIgnoreCaseAndPhoneNumberContaining_ReturnsDrivers_WhenDriversExist() {
        Pageable pageable = mock(Pageable.class);
        Page<Driver> driverPage = mock(Page.class);
        when(driverRepository.findByNameContainingIgnoreCaseAndPhoneNumberContaining("Kirill", "123", pageable))
                .thenReturn(driverPage);

        Page<Driver> resultPage = driverRepository
                .findByNameContainingIgnoreCaseAndPhoneNumberContaining("Kirill", "123", pageable);

        assertEquals(driverPage, resultPage);
        verify(driverRepository, times(1))
                .findByNameContainingIgnoreCaseAndPhoneNumberContaining("Kirill", "123", pageable);
    }
}