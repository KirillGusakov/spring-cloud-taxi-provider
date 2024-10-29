package org.modsen.servicepassenger.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modsen.servicepassenger.controller.PassengerController;
import org.modsen.servicepassenger.dto.request.PassengerRequestDto;
import org.modsen.servicepassenger.dto.response.PassengerResponseDto;
import org.modsen.servicepassenger.service.PassengerService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PassengerController.class)
@DisplayName("Passenger controller unit tests")
public class PassengerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PassengerService passengerService;

    private PassengerResponseDto passengerResponseDto;
    private PassengerRequestDto passengerRequestDto;
    private Page<PassengerResponseDto> passengerPage;

    @BeforeEach
    void setUp() {
        // Given
        passengerResponseDto = new PassengerResponseDto();
        passengerResponseDto.setId(1L);
        passengerResponseDto.setEmail("kirill@example.com");
        passengerResponseDto.setFirstName("Kirill");
        passengerResponseDto.setLastName("Husakou");
        passengerResponseDto.setPhoneNumber("+123456789");

        passengerRequestDto = new PassengerRequestDto();
        passengerRequestDto.setEmail("kirill@example.com");
        passengerRequestDto.setFirstName("Kirill");
        passengerRequestDto.setLastName("Husakou");
        passengerRequestDto.setPhoneNumber("+123456789");

        passengerPage = new PageImpl<>(Collections.singletonList(passengerResponseDto));
    }

    @Test
    void whenFindAllPassengers_thenReturnPassengersList() throws Exception {
        // Given
        when(passengerService.findAll(any(PageRequest.class), eq("kirill@example.com"),
                eq("Kirill"), eq(null), eq(false)))
                .thenReturn(passengerPage);

        // When
        mockMvc.perform(get("/api/v1/passengers")
                        .param("email", "kirill@example.com")
                        .param("name", "Kirill")
                        .param("isDeleted", "false")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengers", hasSize(1)))
                .andExpect(jsonPath("$.passengers[0].email", is("kirill@example.com")))
                .andExpect(jsonPath("$.pageInfo.totalItems", is(1)))
                .andExpect(jsonPath("$.pageInfo.totalPages", is(1)));

        verify(passengerService, times(1)).findAll(any(PageRequest.class),
                eq("kirill@example.com"), eq("Kirill"), eq(null), eq(false));
    }

    @Test
    void whenFindPassengerById_thenReturnPassenger() throws Exception {
        // Given
        when(passengerService.findById(1L)).thenReturn(passengerResponseDto);

        // When
        mockMvc.perform(get("/api/v1/passengers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("kirill@example.com")));

        verify(passengerService, times(1)).findById(1L);
    }

    @Test
    void whenCreatePassenger_thenReturnCreatedPassenger() throws Exception {
        // Given
        when(passengerService.save(any(PassengerRequestDto.class))).thenReturn(passengerResponseDto);

        // When
        mockMvc.perform(post("/api/v1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "kirill@example.com",
                                    "firstName": "Kirill",
                                    "lastName": "Husakou",
                                    "phoneNumber": "+123456789"
                                }
                                """))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("kirill@example.com")));

        verify(passengerService, times(1)).save(any(PassengerRequestDto.class));
    }

    @Test
    void whenUpdatePassenger_thenReturnUpdatedPassenger() throws Exception {
        // Given
        when(passengerService.update(eq(1L), any(PassengerRequestDto.class))).thenReturn(passengerResponseDto);

        // When
        mockMvc.perform(put("/api/v1/passengers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "kirill@example.com",
                                    "firstName": "Kirill",
                                    "lastName": "Husakou",
                                    "phoneNumber": "+123456789"
                                }
                                """))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("kirill@example.com")));

        verify(passengerService, times(1)).update(eq(1L), any(PassengerRequestDto.class));
    }

    @Test
    void whenDeletePassenger_thenReturnNoContent() throws Exception {
        // Given
        doNothing().when(passengerService).delete(1L);

        // When
        mockMvc.perform(delete("/api/v1/passengers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isNoContent());

        verify(passengerService, times(1)).delete(1L);
    }
}
