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
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherIntegrationTest {

    @Autowired
    private WeatherClientImpl weatherClient;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();

        WireMock.configureFor(wireMockServer.port());
    }

    @AfterAll
    static void teardown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("weather.api-url",
                () -> "http://localhost:" + wireMockServer.port() + "/data/2.5/weather/");
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
        assertEquals(285.72, response.getTemperature(), 0.01);
        assertEquals("clear sky", response.getDescription());
    }
}