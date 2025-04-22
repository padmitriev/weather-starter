package dpa.weather.weatherstarter;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dpa.weather.weatherstarter.client.implementation.WeatherClientImpl;
import dpa.weather.weatherstarter.exception.WeatherServiceException;
import dpa.weather.weatherstarter.response.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WeatherIntegrationTest {

    @Autowired
    private WeatherClientImpl weatherClient;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("weather.api-url", () ->
                wireMockServer.baseUrl() + "/data/2.5/weather/");
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

    @Test
    void getWeather_ShouldThrow_WhenCityNotFound() {
        // Arrange
        String city = "UnknownCity";

        wireMockServer.stubFor(get(urlPathEqualTo("/data/2.5/weather/"))
                .withQueryParam("q", equalTo(city))
                .willReturn(aResponse().withStatus(404)));

        // Act & Assert
        assertThrows(WeatherServiceException.class, () -> {
            weatherClient.getWeatherByCity(city);
        });
    }
}