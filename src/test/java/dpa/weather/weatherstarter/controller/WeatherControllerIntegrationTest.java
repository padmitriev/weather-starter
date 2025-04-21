package dpa.weather.weatherstarter.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dpa.weather.weatherstarter.response.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Формируем полный URL с параметрами для тестов
        registry.add("weather.api-url", () ->
                wireMockServer.baseUrl() + "/data/2.5/weather/");

        // Используем тестовый ключ
        registry.add("weather.api-key", () -> "test-key");
    }

    @Test
    void getWeather_IntegrationTest() {
        // Настраиваем WireMock
        wireMockServer.stubFor(get(urlPathEqualTo("/data/2.5/weather/"))
                .withQueryParam("q", equalTo("Paris"))
                .withQueryParam("appid", equalTo("test-key"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "city": "Paris",
                                "temperature": 285.0,
                                "description": "Sunny"
                            }
                            """)
                        .withStatus(200)));


        ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(
                "/weather/Paris", WeatherResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Paris", response.getBody().getCity());
        assertEquals(285.0, response.getBody().getTemperature());
    }
}
