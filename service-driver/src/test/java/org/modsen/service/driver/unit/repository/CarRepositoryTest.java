package org.modsen.service.driver.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.service.driver.model.Car;
import org.modsen.service.driver.repository.CarRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarRepositoryTest {

    @Mock
    private CarRepository carRepository;

    private Car testCar;

    @BeforeEach
    void setUp() {
        testCar = new Car();
        testCar.setId(1L);
        testCar.setNumber("ABC123");
        testCar.setModel("Toyota");
        testCar.setColor("Red");
    }

    @Test
    void carSave_success() {
        when(carRepository.save(testCar)).thenReturn(testCar);

        Car savedCar = carRepository.save(testCar);

        assertNotNull(savedCar);
        assertEquals(testCar.getId(), savedCar.getId());
        assertEquals(testCar.getNumber(), savedCar.getNumber());
        assertEquals(testCar.getModel(), savedCar.getModel());
        assertEquals(testCar.getColor(), savedCar.getColor());
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void testFindById_returnsCar_WhenCarExists() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        Optional<Car> foundCar = carRepository.findById(1L);

        assertTrue(foundCar.isPresent());
        assertEquals(testCar, foundCar.get());
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_returnsEmpty_WhenCarDoesNotExist() {
        when(carRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Car> foundCar = carRepository.findById(2L);

        assertFalse(foundCar.isPresent());
        verify(carRepository, times(1)).findById(2L);
    }

    @Test
    void updateCar_success() {
        when(carRepository.save(testCar)).thenReturn(testCar);

        Car carToUpdate = testCar;
        carToUpdate.setModel("Honda");

        Car updatedCar = carRepository.save(carToUpdate);

        assertNotNull(updatedCar);
        assertEquals("Honda", updatedCar.getModel());
        verify(carRepository, times(1)).save(carToUpdate);
    }

    @Test
    void deleteCar_success() {
        carRepository.deleteById(1L);
        verify(carRepository, times(1)).deleteById(1L);
    }

    @Test
    void existsByNumberAndIdNot_returnTrue() {
        when(carRepository.existsByNumberAndIdNot("ABC123", 0L)).thenReturn(true);

        boolean exists = carRepository.existsByNumberAndIdNot("ABC123", 0L);

        assertTrue(exists);
        verify(carRepository, times(1)).existsByNumberAndIdNot("ABC123", 0L);
    }

    @Test
    void existsByNumberAndIdNot_returnsFalse() {
        when(carRepository.existsByNumberAndIdNot("XYZ456", 1L)).thenReturn(false);

        boolean exists = carRepository.existsByNumberAndIdNot("XYZ456", 1L);

        assertFalse(exists);
        verify(carRepository, times(1)).existsByNumberAndIdNot("XYZ456", 1L);
    }
}