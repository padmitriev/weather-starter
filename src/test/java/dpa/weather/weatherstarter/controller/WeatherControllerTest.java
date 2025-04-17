package dpa.weather.weatherstarter.controller;

import static org.junit.jupiter.api.Assertions.*;

import dpa.weather.weatherstarter.exception.WeatherServiceException;
import dpa.weather.weatherstarter.responce.WeatherResponse;
import dpa.weather.weatherstarter.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void getWeather_ShouldReturnWeatherData_WhenCityExists() throws Exception {
        // Arrange
        String city = "London";
        WeatherResponse mockResponse = new WeatherResponse();
        mockResponse.setCity(city);
        mockResponse.setTemperature(15.5);
        mockResponse.setDescription("Cloudy");

        given(weatherService.getWeatherForCity(city)).willReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/weather/{city}", city)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value(city))
                .andExpect(jsonPath("$.temperature").value(15.5))
                .andExpect(jsonPath("$.description").value("Cloudy"));
    }

    @Test
    void getWeather_ShouldReturn400_WhenCityIsEmpty() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/weather/ ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWeather_ShouldReturn500_WhenServiceError() throws Exception {
        // Arrange
        String city = "London";
        given(weatherService.getWeatherForCity(city))
                .willThrow(new RuntimeException("Internal server error"));

        // Act & Assert
        mockMvc.perform(get("/weather/{city}", city)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}