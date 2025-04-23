package dpa.weather.weatherstarter;

import dpa.weather.weatherstarter.client.implementation.WeatherClientImpl;
import dpa.weather.weatherstarter.exception.WeatherServiceException;
import dpa.weather.weatherstarter.response.WeatherResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherIntegrationTest {

    @Autowired
    private WeatherClientImpl weatherClient;

    private static final int WIREMOCK_PORT = 8084;

    private static final WireMockServer wireMockServer = new WireMockServer(WIREMOCK_PORT);

    @BeforeAll
    static void setup() {
        wireMockServer.start();
    }

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("weather.api-url", () -> "http://localhost:" + WIREMOCK_PORT + "/data/2.5/weather/");
        registry.add("weather.api-key", () -> "test-key");
    }

    @Test
    void getWeather_ShouldReturnWeatherData_WhenCityExists() throws WeatherServiceException {
        // Arrange
        String city = "Paris";
        String mockResponse = """
                {
                    "city": "Paris",
                    "temperature": 285.72,
                    "description": "clear sky"
                }
            """;

        wireMockServer.stubFor(get(urlPathEqualTo("/data/2.5/weather/"))
                .withQueryParam("q", equalTo(city))
                .withQueryParam("appid", equalTo("test-key"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponse)
                        .withStatus(200)));

        // Act
        WeatherResponse response = weatherClient.getWeatherByCity(city);

        // Assert
        assertNotNull(response);
        assertEquals(city, response.getCity());
        assertEquals(285.72, response.getTemperature());
        assertEquals("clear sky", response.getDescription());
    }
}