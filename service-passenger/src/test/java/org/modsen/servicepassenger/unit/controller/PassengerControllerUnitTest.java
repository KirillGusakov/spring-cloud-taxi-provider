package org.modsen.servicepassenger.unit.controller;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modsen.servicepassenger.controller.PassengerController;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PageResponse;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.service.PassengerService;
import org.modsen.servicepassenger.util.PassengerTestUtil;
import org.modsen.servicepassenger.util.SecurityTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.HashMap;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PassengerController.class)
@DisplayName("Passenger controller unit tests")
public class PassengerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerService passengerService;

    private PassengerResponseDto passengerResponseDto;
    private PassengerRequestDto passengerRequestDto;
    private Page<PassengerResponseDto> passengerPage;
    private String token;

    @BeforeEach
    void setUp() {
        passengerResponseDto = PassengerTestUtil.responseDto;
        passengerRequestDto = PassengerTestUtil.passengerRequestDto;
        token = SecurityTestUtil.obtainAccessToken();
    }

    @Test
    void whenFindAllPassengers_thenReturnPassengersList() throws Exception {
        // Given
        Page<PassengerResponseDto> passengerPage = new PageImpl<>(Collections.singletonList(
                new PassengerResponseDto(1L, "Kirill", "Example", "kirill@example.com", "123456789", false)
        ));
        when(passengerService.findAll(any(Pageable.class), eq("kirill@example.com"),
                eq("Kirill"), eq(null), eq(false)))
                .thenReturn(createPassengerResponse(passengerPage));

        // When
        mockMvc.perform(get("/api/v1/passengers")
                        .param("email", "kirill@example.com")
                        .param("name", "Kirill")
                        .param("isDeleted", "false")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengers", hasSize(1)))
                .andExpect(jsonPath("$.passengers[0].email", is("kirill@example.com")))
                .andExpect(jsonPath("$.pageInfo.totalItems", is(1)))
                .andExpect(jsonPath("$.pageInfo.totalPages", is(1)));

        verify(passengerService, times(1)).findAll(any(Pageable.class),
                eq("kirill@example.com"), eq("Kirill"), eq(null), eq(false));
    }

    @Test
    void whenFindPassengerById_thenReturnPassenger() throws Exception {
        // Given
        when(passengerService.findById(1L, JWT.decode(token).getSubject())).thenReturn(passengerResponseDto);

        // When
        mockMvc.perform(get("/api/v1/passengers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("kirill.kirill@example.com")));

        verify(passengerService, times(1)).findById(1L, JWT.decode(token).getSubject());
    }

    @Test
    void whenCreatePassenger_thenReturnCreatedPassenger() throws Exception {
        // Given
        when(passengerService.save(any(PassengerRequestDto.class), any(String.class))).thenReturn(passengerResponseDto);

        // When
        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto))
                        .header("Authorization", "Bearer " + token))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("kirill.kirill@example.com")));

        verify(passengerService, times(1)).save(any(PassengerRequestDto.class), any(String.class));
    }

    @Test
    void whenUpdatePassenger_thenReturnUpdatedPassenger() throws Exception {
        // Given
        when(passengerService.update(eq(1L), any(PassengerRequestDto.class), any(String.class))).thenReturn(passengerResponseDto);

        // When
        mockMvc.perform(put("/api/v1/passengers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto))
                        .header("Authorization", "Bearer " + token))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("kirill.kirill@example.com")));

        verify(passengerService, times(1)).update(eq(1L), any(PassengerRequestDto.class), any(String.class));
    }

    @Test
    void whenDeletePassenger_thenReturnNoContent() throws Exception {
        // Given
        doNothing().when(passengerService).delete(1L, JWT.decode(token).getSubject());

        // When
        mockMvc.perform(delete("/api/v1/passengers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                // Then
                .andExpect(status().isNoContent());

        verify(passengerService, times(1)).delete(1L, JWT.decode(token).getSubject());
    }

    private Map<String, Object> createPassengerResponse(Page<PassengerResponseDto> passengerPage) {
        Map<String, Object> response = new HashMap<>();
        PageResponse pageResponse = PageResponse.builder()
                .currentPage(passengerPage.getNumber())
                .totalItems(passengerPage.getTotalElements())
                .totalPages(passengerPage.getTotalPages())
                .pageSize(passengerPage.getSize())
                .build();
        response.put("passengers", passengerPage.getContent());
        response.put("pageInfo", pageResponse);
        return response;
    }
}
