package org.modsen.servicerating.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modsen.servicerating.controller.RatingController;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingController.class)
public class RatingControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    private RatingResponse ratingResponse;
    private RatingRequest ratingRequest;
    private Page<RatingResponse> ratingPage;

    @BeforeEach
    void setUp() {
        ratingResponse = RatingResponse.builder()
                .id(1L)
                .driverId(100L)
                .userId(200L)
                .driverRating(5)
                .passengerRating(4)
                .comment("Great ride!")
                .build();

        ratingRequest = RatingRequest.builder()
                .driverRating(5)
                .passengerRating(4)
                .comment("Great ride!")
                .build();

        ratingPage = new PageImpl<>(Collections.singletonList(ratingResponse));
    }

    @Test
    void givenValidRequest_whenFindAllRatings_thenReturnRatings() throws Exception {
        // Given
        when(ratingService.findAll(any(PageRequest.class), eq(100L), eq(200L), eq(5)))
                .thenReturn(ratingPage);

        // When
        mockMvc.perform(get("/api/v1/ratings")
                        .param("driverId", "100")
                        .param("userId", "200")
                        .param("driverRating", "5")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratings", hasSize(1)))
                .andExpect(jsonPath("$.ratings[0].driverId", is(100)))
                .andExpect(jsonPath("$.pageInfo.totalItems", is(1)));

        verify(ratingService, times(1)).findAll(any(PageRequest.class), eq(100L), eq(200L), eq(5));
    }

    @Test
    void givenExistingId_whenFindById_thenReturnRating() throws Exception {
        // Given
        when(ratingService.findById(1L)).thenReturn(ratingResponse);

        // When
        mockMvc.perform(get("/api/v1/ratings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.driverId", is(100)))
                .andExpect(jsonPath("$.userId", is(200)));

        verify(ratingService, times(1)).findById(1L);
    }

    @Test
    void givenValidRequest_whenUpdateRating_thenReturnUpdatedRating() throws Exception {
        // Given
        when(ratingService.update(eq(1L), any(RatingRequest.class))).thenReturn(ratingResponse);

        // When
        mockMvc.perform(put("/api/v1/ratings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "driverRating": 5,
                                    "passengerRating": 4,
                                    "comment": "Great ride!"
                                }
                                """))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverRating", is(5)))
                .andExpect(jsonPath("$.passengerRating", is(4)));

        verify(ratingService, times(1)).update(eq(1L), any(RatingRequest.class));
    }

    @Test
    void givenExistingId_whenDeleteRating_thenReturnNoContent() throws Exception {
        // Given
        doNothing().when(ratingService).delete(1L);

        // When
        mockMvc.perform(delete("/api/v1/ratings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isNoContent());

        verify(ratingService, times(1)).delete(1L);
    }

    @Test
    void givenValidDriverId_whenFindDriverAverageRating_thenReturnAverageRating() throws Exception {
        // Given
        when(ratingService.getAverageRatingForDriver(100L)).thenReturn(4.5);

        // When
        mockMvc.perform(get("/api/v1/ratings/driver/{id}/avg", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(4.5)));

        verify(ratingService, times(1)).getAverageRatingForDriver(100L);
    }
}
