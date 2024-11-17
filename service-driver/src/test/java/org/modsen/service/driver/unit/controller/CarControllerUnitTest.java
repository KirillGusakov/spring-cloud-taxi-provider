package org.modsen.service.driver.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modsen.service.driver.controller.CarController;
import org.modsen.service.driver.dto.request.CarRequestDto;
import org.modsen.service.driver.dto.response.CarResponseDto;
import org.modsen.service.driver.service.CarService;
import org.modsen.service.driver.util.CarTestUtil;
import org.modsen.service.driver.util.SecurityTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(CarController.class)
public class CarControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarService carService;
    private CarRequestDto carRequestDto;
    private CarResponseDto carResponseDto;
    private Page<CarResponseDto> carPage;
    private String token;

    @BeforeEach
    public void setUp() {
        carRequestDto = CarTestUtil.carRequest;
        carResponseDto = CarTestUtil.carResponse;

        token = SecurityTestUtils.obtainAccessToken();
    }


    @Test
    void givenCarDetails_whenSaveCar_thenStatusCreatedAndReturnCar() throws Exception {
        // Given
        when(carService.save(any(CarRequestDto.class))).thenReturn(carResponseDto);

        // When & Then
        mockMvc.perform(post("/api/v1/cars")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(carRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.number", is("AA-7777-7")))
                .andExpect(status().isCreated());

        verify(carService, times(1)).save(carRequestDto);
    }

    @Test
    void givenCarDetails_whenUpdateCar_thenStatusOkAndReturnUpdatedCar() throws Exception {
        // Given
        when(carService.update(eq(1L), any(CarRequestDto.class))).thenReturn(carResponseDto);

        // When & Then
        mockMvc.perform(put("/api/v1/cars/1")
                        .content(objectMapper.writeValueAsString(carRequestDto))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number", is("AA-7777-7")))
                .andExpect(status().isOk());

        verify(carService, times(1)).update(eq(1L), any(CarRequestDto.class));
    }

    @Test
    void givenCarId_whenDeleteCar_thenStatusNoContent() throws Exception {
        // Given
        doNothing().when(carService).deleteCar(eq(1L));

        // When & Then
        mockMvc.perform(delete("/api/v1/cars/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        verify(carService, times(1)).deleteCar(eq(1L));
    }

    @Test
    void givenCarId_whenFindById_thenStatusOkAndReturnCar() throws Exception {
        // Given
        when(carService.findById(eq(1L))).thenReturn(carResponseDto);

        // When & Then
        mockMvc.perform(get("/api/v1/cars/{id}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is("AA-7777-7")))
                .andExpect(jsonPath("$.model", is("BMW")));

        verify(carService, times(1)).findById(eq(1L));
    }

    @Test
    void givenPageRequest_whenFindAllCars_thenStatusOkAndReturnCarsList() throws Exception {
        // Given
        List<CarResponseDto> carList = Arrays.asList(carResponseDto);
        carPage = new PageImpl<>(carList, PageRequest.of(0, 10), carList.size());

        when(carService.findAll(any(PageRequest.class), any(String.class), any(String.class)))
                .thenReturn(carPage);

        // When & Then
        mockMvc.perform(get("/api/v1/cars")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .param("model", "BMW")
                        .param("number", "AA-7777-7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cars", hasSize(1)))
                .andExpect(jsonPath("$.cars[0].model", is("BMW")))
                .andExpect(jsonPath("$.cars[0].number", is("AA-7777-7")))
                .andExpect(jsonPath("$.pageInfo.totalItems", is(1)))
                .andExpect(jsonPath("$.pageInfo.totalPages", is(1)));

        verify(carService, times(1)).findAll(any(PageRequest.class), any(String.class), any(String.class));
    }
}
