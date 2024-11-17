package org.modsen.serviceride.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modsen.serviceride.controller.RideController;
import org.modsen.serviceride.dto.request.RideRequest;
import org.modsen.serviceride.dto.request.RideUpdateRequest;
import org.modsen.serviceride.dto.response.RideResponse;
import org.modsen.serviceride.service.RideService;
import org.modsen.serviceride.util.SecurityTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RideController.class)
public class RideControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RideService rideService;

    private RideResponse rideResponse;
    private RideRequest rideRequest;
    private Page<RideResponse> ridePage;
    private String token;

    @BeforeEach
    void setUp() {
        token = SecurityTestUtils.obtainAccessToken();

        // Given
        rideResponse = RideResponse.builder()
                .id(1L)
                .driverId(100L)
                .passengerId(200L)
                .pickupAddress("123 Main St")
                .destinationAddress("456 Elm St")
                .status("CREATED")
                .orderTime(LocalDateTime.now())
                .price(BigDecimal.valueOf(10.0))
                .build();

        rideRequest = RideRequest.builder()
                .driverId(100L)
                .passengerId(200L)
                .pickupAddress("123 Main St")
                .destinationAddress("456 Elm St")
                .status("CREATED")
                .price(BigDecimal.valueOf(10.0))
                .build();

        ridePage = new PageImpl<>(Collections.singletonList(rideResponse));
    }

    @Test
    void getAllRides_success() throws Exception {
        // Given
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("rides", List.of(rideResponse));

        when(rideService.findAll(any(PageRequest.class), any())).thenReturn(hashMap);

        // When
        mockMvc.perform(get("/api/v1/rides")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides", hasSize(1)))
                .andExpect(jsonPath("$.rides[0].driverId", is(100)));

        verify(rideService, times(1)).findAll(any(PageRequest.class), any());
    }

    @Test
    void getRideById_success() throws Exception {
        // Given
        when(rideService.findById(1L)).thenReturn(rideResponse);

        // When
        mockMvc.perform(get("/api/v1/rides/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.driverId", is(100)))
                .andExpect(jsonPath("$.passengerId", is(200)));

        verify(rideService, times(1)).findById(1L);
    }

    @Test
    void saveRide_success() throws Exception {
        // Given
        when(rideService.save(any(RideRequest.class))).thenReturn(rideResponse);

        // When
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "driverId": 100,
                                    "passengerId": 200,
                                    "pickupAddress": "123 Main St",
                                    "destinationAddress": "456 Elm St",
                                    "status": "CREATED",
                                    "price": 10.0
                                }
                                """)
                        .header("Authorization", "Bearer " + token))

                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.driverId", is(100)))
                .andExpect(jsonPath("$.passengerId", is(200)))
                .andExpect(jsonPath("$.pickupAddress", is("123 Main St")))
                .andExpect(jsonPath("$.destinationAddress", is("456 Elm St")));

        verify(rideService, times(1)).save(any(RideRequest.class));
    }

    @Test
    void updateRide_success() throws Exception {
        // Given
        when(rideService.update(eq(1L), any(RideUpdateRequest.class))).thenReturn(rideResponse);

        // When
        mockMvc.perform(put("/api/v1/rides/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "driverId": 100,
                                    "passengerId": 200,
                                    "pickupAddress": "123 Main St",
                                    "destinationAddress": "456 Elm St",
                                    "price": 10.0
                                }
                                """)
                        .header("Authorization", "Bearer " + token))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId", is(100)))
                .andExpect(jsonPath("$.passengerId", is(200)));

        verify(rideService, times(1)).update(eq(1L), any(RideUpdateRequest.class));
    }

    @Test
    void deleteRide_success() throws Exception {
        // Given
        doNothing().when(rideService).delete(1L);

        // When
        mockMvc.perform(delete("/api/v1/rides/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                // Then
                .andExpect(status().isNoContent());

        verify(rideService, times(1)).delete(1L);
    }

    @Test
    void updateRideStatus_success() throws Exception {
        // Given
        rideResponse.setStatus("ACCEPTED");
        when(rideService.updateRideStatus(1L, "ACCEPTED")).thenReturn(rideResponse);

        // When
        mockMvc.perform(patch("/api/v1/rides/{id}/status", 1L)
                        .param("status", "ACCEPTED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACCEPTED")));

        verify(rideService, times(1)).updateRideStatus(1L, "ACCEPTED");
    }
}