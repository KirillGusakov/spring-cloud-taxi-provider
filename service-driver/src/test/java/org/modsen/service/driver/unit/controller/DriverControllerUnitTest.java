package org.modsen.service.driver.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modsen.service.driver.controller.DriverController;
import org.modsen.service.driver.dto.request.DriverRequestDto;
import org.modsen.service.driver.dto.response.DriverResponseDto;
import org.modsen.service.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
public class DriverControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;

    private DriverRequestDto driverRequestDto;
    private DriverResponseDto driverResponseDto;

    @BeforeEach
    public void setUp() {
        driverRequestDto = DriverRequestDto.builder()
                .name("Kirill")
                .phoneNumber("+1234567890")
                .sex("M")
                .build();

        driverResponseDto = DriverResponseDto.builder()
                .id(1L)
                .name("Kirill")
                .phoneNumber("+1234567890")
                .sex("M")
                .build();
    }

    @Test
    void givenValidDriverRequest_whenSaveDriver_thenReturnsCreatedDriver() throws Exception {
        // given
        when(driverService.saveDriver(any(DriverRequestDto.class))).thenReturn(driverResponseDto);

        // when
        mockMvc.perform(post("/api/v1/drivers")
                        .content("""
                                  {
                                  "name": "Kirill",
                                  "phoneNumber": "+1234567890",
                                  "sex": "M"
                                  }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))

                // then
                .andExpect(jsonPath("$.name", is("Kirill")))
                .andExpect(status().isCreated());

        verify(driverService, times(1)).saveDriver(driverRequestDto);
    }

    @Test
    void givenValidDriverRequest_whenUpdateDriver_thenReturnsUpdatedDriver() throws Exception {
        // given
        when(driverService.updateDriver(eq(1L), any(DriverRequestDto.class))).thenReturn(driverResponseDto);

        // when
        mockMvc.perform(put("/api/v1/drivers/1")
                        .content("""
                          {
                          "name": "Kirill",
                          "phoneNumber": "+1234567890",
                          "sex": "M"
                          }
                        """)
                        .contentType(MediaType.APPLICATION_JSON))

                // then
                .andExpect(jsonPath("$.name", is("Kirill")))
                .andExpect(status().isOk());

        verify(driverService, times(1)).updateDriver(eq(1L), any(DriverRequestDto.class));
    }

    @Test
    void givenDriverId_whenDeleteDriver_thenReturnsNoContent() throws Exception {
        // given
        doNothing().when(driverService).deleteDriver(eq(1L));

        // when
        mockMvc.perform(delete("/api/v1/drivers/1"))

                // then
                .andExpect(status().isNoContent());

        verify(driverService, times(1)).deleteDriver(eq(1L));
    }

    @Test
    void givenDriverId_whenFindDriverById_thenReturnsDriver() throws Exception {
        // given
        when(driverService.getDriver(eq(1L))).thenReturn(driverResponseDto);

        // when
        mockMvc.perform(get("/api/v1/drivers/{id}", 1L))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Kirill")))
                .andExpect(jsonPath("$.phoneNumber", is("+1234567890")));

        verify(driverService, times(1)).getDriver(eq(1L));
    }

    @Test
    void givenDriversQueryParams_whenFindAllDrivers_thenReturnsDriverList() throws Exception {
        // given
        List<DriverResponseDto> driverList = Arrays.asList(driverResponseDto);
        when(driverService.getDrivers(any(), any(String.class), any(String.class)))
                .thenReturn(new PageImpl<>(driverList));

        // when
        mockMvc.perform(get("/api/v1/drivers")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .param("name", "Kirill")
                        .param("phone", "+1234567890")
                        .contentType(MediaType.APPLICATION_JSON))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.drivers", hasSize(1)))
                .andExpect(jsonPath("$.drivers[0].name", is("Kirill")))
                .andExpect(jsonPath("$.drivers[0].phoneNumber", is("+1234567890")));

        verify(driverService, times(1)).getDrivers(any(), any(String.class), any(String.class));
    }
}