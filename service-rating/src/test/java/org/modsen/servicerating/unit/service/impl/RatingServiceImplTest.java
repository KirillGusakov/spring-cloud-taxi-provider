package org.modsen.servicerating.unit.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.servicerating.client.DriverClient;
import org.modsen.servicerating.client.PassengerClient;
import org.modsen.servicerating.dto.message.RatingMessage;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.mapper.RatingMapper;
import org.modsen.servicerating.model.Rating;
import org.modsen.servicerating.repository.RatingRepository;
import org.modsen.servicerating.service.impl.RatingServiceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private DriverClient driverClient;

    @Mock
    private PassengerClient passengerClient;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private Rating rating;
    private RatingResponse ratingResponse;
    private RatingRequest ratingRequest;

    @BeforeEach
    void setUp() {
        rating = Rating.builder()
                .id(1L)
                .driverId(2L)
                .userId(3L)
                .rideId(4L)
                .build();

        ratingResponse = RatingResponse.builder()
                .id(1L)
                .driverId(2L)
                .userId(3L)
                .rideId(4L)
                .driverRating(5)
                .passengerRating(4)
                .comment("Good driver")
                .build();

        ratingRequest = RatingRequest.builder()
                .driverRating(5)
                .passengerRating(4)
                .comment("Good driver")
                .build();
    }

    @Test
    void findById_success() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingMapper.toRatingResponse(rating)).thenReturn(ratingResponse);

        RatingResponse foundRating = ratingService.findById(1L);

        assertNotNull(foundRating);
        assertEquals(ratingResponse, foundRating);
        verify(ratingRepository, times(1)).findById(1L);
    }

    @Test
    void findById_notFound() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> ratingService.findById(1L));
        verify(ratingRepository, times(1)).findById(1L);
    }

    @Test
    void findAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rating> ratings = new PageImpl<>(Collections.singletonList(rating));

        when(ratingRepository.findByFilter(any(Long.class), any(Long.class), any(Integer.class), eq(pageable)))
                .thenReturn(ratings);
        when(ratingMapper.toRatingResponse(rating)).thenReturn(ratingResponse);

        Page<RatingResponse> result = ratingService.findAll(pageable, 2L, 3L, 5);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(ratingRepository, times(1)).findByFilter(any(Long.class), any(Long.class), any(Integer.class), eq(pageable));
    }

    @Test
    void saveRatingMessage_success() {
        RatingMessage ratingMessage = new RatingMessage(2L, 3L, 4L);
        Rating savedRating = Rating.builder().driverId(2L).userId(3L).rideId(4L).build();

        when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);

        ratingService.consumeRating(ratingMessage);

        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void update_success() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingMapper.toRatingResponse(rating)).thenReturn(ratingResponse);

        RatingResponse result = ratingService.update(1L, ratingRequest);

        assertNotNull(result);
        assertEquals(ratingResponse, result);
        verify(ratingRepository, times(1)).save(rating);
    }

    @Test
    void delete_success() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        ratingService.delete(1L);

        verify(ratingRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAverageRatingForDriver_success() {
        when(ratingRepository.findAverageRatingByDriverId(2L)).thenReturn(Optional.of(4.5));

        Double avgRating = ratingService.getAverageRatingForDriver(2L);

        assertNotNull(avgRating);
        assertEquals(4.5, avgRating);
        verify(ratingRepository, times(1)).findAverageRatingByDriverId(2L);
    }
}
