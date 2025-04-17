package dpa.weather.weatherstarter.client;

import dpa.weather.weatherstarter.exception.WeatherServiceException;
import dpa.weather.weatherstarter.properties.WeatherProperties;
import dpa.weather.weatherstarter.responce.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherProperties weatherProperties;

    @InjectMocks
    private WeatherClient weatherClient;

    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "5f8f1e322944e656897cbd9549859b6b";

    @BeforeEach
    void setUp(){
        // Настройка моков перед каждым тестом
        when(weatherProperties.getApiUrl()).thenReturn(API_URL);
        when(weatherProperties.getApiKey()).thenReturn(API_KEY);
    }

    @Test
    void getWeatherByCity_ShouldReturnValidResponse_WhenRequestIsSuccessful() throws URISyntaxException {
        // Arrange
        String city = "London";

        WeatherResponse expectedResponse = new WeatherResponse();
        expectedResponse.setTemperature(15.5);
        expectedResponse.setDescription("Cloudy");

        URI expectedUri = new URI(API_URL + "?q=" + city + "&appid=" + API_KEY);

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                argThat(request -> {
                    HttpHeaders headers = request.getHeaders();
                    return headers.getAccept().contains(MediaType.APPLICATION_JSON);
                }),
                eq(WeatherResponse.class))
        ).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        WeatherResponse actualResponse = weatherClient.getWeatherByCity(city);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(city, actualResponse.getCity());
        assertEquals(15.5, actualResponse.getTemperature());
        assertEquals("Cloudy", actualResponse.getDescription());
    }

    @Test
    void getWeatherByCity_ShouldThrowException_WhenApiReturnsError() throws URISyntaxException {
        // Arrange
        String city = "InvalidCity";

        URI expectedUri = new URI(API_URL + "?q=" + city + "&appid=" + API_KEY);

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(WeatherResponse.class))
        ).thenThrow(new RestClientResponseException(
                "City not found",
                404,
                "Not Found",
                HttpHeaders.EMPTY,
                "City not found".getBytes(),
                null));

        // Act & Assert
        WeatherServiceException exception = assertThrows(WeatherServiceException.class,
                () -> weatherClient.getWeatherByCity(city));

        assertTrue(exception.getMessage().contains("404"));
        assertTrue(exception.getMessage().contains("City not found"));
    }

    @Test
    void getWeatherByCity_ShouldThrowException_WhenConnectionFails() throws URISyntaxException {
        // Arrange
        String city = "London";
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather";
        String apiKey = "test-api-key";

        URI expectedUri = new URI(apiUrl + "?q=" + city + "&appid=" + apiKey);

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(WeatherResponse.class))
        ).thenThrow(new ResourceAccessException("Connection timeout"));

        // Act & Assert
        WeatherServiceException exception = assertThrows(WeatherServiceException.class,
                () -> weatherClient.getWeatherByCity(city));

        assertTrue(exception.getMessage().contains("Connection to weather service failed"));
    }

    @Test
    void getWeatherByCity_ShouldThrowException_WhenCityIsEmpty() {
        // Arrange
        String emptyCity = "";

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> weatherClient.getWeatherByCity(emptyCity));
    }

    @Test
    void buildWeatherUrl_ShouldThrowException_WhenApiUrlIsInvalid() {
        // Arrange
        String invalidUrl = "invalid-url";
        when(weatherProperties.getApiUrl()).thenReturn(invalidUrl);
        when(weatherProperties.getApiKey()).thenReturn("test-key");

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> weatherClient.getWeatherByCity("London"));
    }
}
