package org.modsen.servicerating.unit.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modsen.servicerating.model.Rating;
import org.modsen.servicerating.repository.RatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RatingRepositoryTest {

    @Mock
    private RatingRepository ratingRepository;

    private Rating rating;

    @BeforeEach
    public void setUp() {
        rating = Rating.builder()
                .id(1L)
                .driverId(1L)
                .userId(1L)
                .rideId(1L)
                .driverRating(5)
                .passengerRating(4)
                .comment("Good")
                .build();
    }

    @Test
    public void testFindAverageRatingByDriverId() {
        when(ratingRepository.findAverageRatingByDriverId(rating.getDriverId())).thenReturn(Optional.of(5.0));

        Optional<Double> actualAverage = ratingRepository.findAverageRatingByDriverId(rating.getDriverId());

        assertThat(actualAverage).isPresent();
        assertThat(actualAverage.get()).isEqualTo(5.0);
    }

    @Test
    public void testFindByFilter() {
        Long driverId = 1L;
        Long userId = null;
        Integer ratingValue = null;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Rating> expectedPage = new PageImpl<>(Collections.singletonList(rating), pageable, 1);

        when(ratingRepository.findByFilter(driverId, userId, ratingValue, pageable)).thenReturn(expectedPage);

        Page<Rating> actualPage = ratingRepository.findByFilter(driverId, userId, ratingValue, pageable);

        assertThat(actualPage.getContent()).hasSize(1);
        assertThat(actualPage.getContent().get(0).getDriverRating()).isEqualTo(5);
    }

    @Test
    public void testSaveRating() {
        when(ratingRepository.save(rating)).thenReturn(rating);

        Rating savedRating = ratingRepository.save(rating);

        assertThat(savedRating).isNotNull();
        assertThat(savedRating.getDriverRating()).isEqualTo(5);
    }

    @Test
    public void testDeleteRating() {
        doNothing().when(ratingRepository).delete(rating);

        ratingRepository.delete(rating);

        verify(ratingRepository, times(1)).delete(rating);
    }

    @Test
    public void testFindById() {
        when(ratingRepository.findById(rating.getId())).thenReturn(Optional.of(rating));

        Optional<Rating> foundRating = ratingRepository.findById(rating.getId());

        assertThat(foundRating).isPresent();
        assertThat(foundRating.get().getId()).isEqualTo(rating.getId());
    }

    @Test
    public void testFindAll() {
        when(ratingRepository.findAll()).thenReturn(Collections.singletonList(rating));

        List<Rating> allRatings = ratingRepository.findAll();

        assertThat(allRatings).isNotEmpty();
        assertThat(allRatings.size()).isEqualTo(1);
    }

    @Test
    public void testUpdateRating() {
        when(ratingRepository.save(rating)).thenReturn(rating);

        rating.setDriverRating(4);
        Rating updatedRating = ratingRepository.save(rating);

        assertThat(updatedRating.getDriverRating()).isEqualTo(4);
    }
}
