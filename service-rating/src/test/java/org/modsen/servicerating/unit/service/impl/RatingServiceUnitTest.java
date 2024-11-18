package org.modsen.servicerating.unit.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.servicerating.dto.message.RatingMessage;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.AverageRating;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.mapper.RatingMapper;
import org.modsen.servicerating.model.Rating;
import org.modsen.servicerating.repository.RatingRepository;
import org.modsen.servicerating.service.impl.RatingServiceImpl;
import org.modsen.servicerating.util.DoRequestUtil;
import org.modsen.servicerating.util.SecurityTestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.Map;
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
public class RatingServiceUnitTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private DoRequestUtil doRequestUtil;

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
    void givenExistingId_whenFindById_thenReturnRating() {
        // Given
        SecurityTestUtils.setUpSecurityContextWithRole("ROLE_USER");
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingMapper.toRatingResponse(rating)).thenReturn(ratingResponse);

        // When
        RatingResponse foundRating = ratingService.findById(1L);

        // Then
        assertNotNull(foundRating);
        assertEquals(ratingResponse, foundRating);
        verify(ratingRepository, times(1)).findById(1L);
    }

    @Test
    void givenNonExistingId_whenFindById_thenThrowException() {
        // Given
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> ratingService.findById(1L));
        verify(ratingRepository, times(1)).findById(1L);
    }

    @Test
    void givenPageRequest_whenFindAll_thenReturnRatings() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rating> ratings = new PageImpl<>(Collections.singletonList(rating));

        when(ratingRepository.findByFilter(any(Long.class), any(Long.class), any(Integer.class), eq(pageable)))
                .thenReturn(ratings);
        when(ratingMapper.toRatingResponse(rating)).thenReturn(ratingResponse);

        // When
        Map<String, Object> all = ratingService.findAll(pageable, 2L, 3L, 5);

        // Then
        assertNotNull(all);
        verify(ratingRepository, times(1)).findByFilter(any(Long.class), any(Long.class), any(Integer.class), eq(pageable));
    }

    @Test
    void givenRatingMessage_whenSaveRatingMessage_thenSaveRating() {
        // Given
        RatingMessage ratingMessage = new RatingMessage(2L, 3L, 4L);
        Rating savedRating = Rating.builder().driverId(2L).userId(3L).rideId(4L).build();

        when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);

        // When
        ratingService.consumeRating(ratingMessage);

        // Then
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void givenValidRequest_whenUpdate_thenReturnUpdatedRating() {
        // Given
        SecurityTestUtils.setUpSecurityContextWithRole("ROLE_USER");
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingMapper.toRatingResponse(rating)).thenReturn(ratingResponse);

        // When
        RatingResponse result = ratingService.update(1L, ratingRequest);

        // Then
        assertNotNull(result);
        assertEquals(ratingResponse, result);
        verify(ratingRepository, times(1)).save(rating);
    }

    @Test
    void givenExistingId_whenDelete_thenDeleteRating() {
        // Given
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        // When
        ratingService.delete(1L);

        // Then
        verify(ratingRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenExistingDriverId_whenGetAverageRatingForDriver_thenReturnAverageRating() {
        // Given
        when(ratingRepository.findAverageRatingByDriverId(2L)).thenReturn(Optional.of(4.5));

        // When
        AverageRating avg = ratingService.getAverageRatingForDriver(2L);

        // Then
        assertNotNull(avg.getAverageRating());
        assertEquals(4.5, avg.getAverageRating());
        verify(ratingRepository, times(1)).findAverageRatingByDriverId(2L);
    }
}
