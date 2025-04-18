package dpa.weather.weatherstarter.client;

import dpa.weather.weatherstarter.exception.WeatherServiceException;
import dpa.weather.weatherstarter.properties.WeatherProperties;
import dpa.weather.weatherstarter.responce.WeatherResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

class WeatherClientTest {

    private WireMockServer wireMockServer;
    private WeatherClient weatherClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);

        WeatherProperties properties = new WeatherProperties();
        properties.setApiUrl("http://localhost:8080/data/2.5/weather/");
        properties.setApiKey("test-api-key");

        weatherClient = new WeatherClient(new RestTemplate(), properties);
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void getWeatherByCity_ShouldReturnWeatherResponse() throws WeatherServiceException {
        stubFor(get(urlPathEqualTo("/data/2.5/weather/"))
                .withQueryParam("q", equalTo("Paris"))
                .withQueryParam("appid", equalTo("test-api-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                        {
                            "weather": [{"description": "Sunny"}],
                            "main": {"temp": 22.0, "feels_like": 20.5, "humidity": 60},
                            "sys": {"country": "FR"}
                        }
                        """)));

        WeatherResponse response = weatherClient.getWeatherByCity("Paris");

        assertNotNull(response, "Response should not be null");
        assertEquals("Paris", response.getCity());
        assertEquals(22.0, response.getTemperature());
        assertEquals("Sunny", response.getDescription());
        assertEquals("FR", response.getCountry());
    }

    @Test
    void getWeatherByCity_ShouldThrowException_WhenCityIsInvalid() {
        // 1. Пустое название города
        assertThrows(IllegalArgumentException.class, () -> {
            weatherClient.getWeatherByCity("");
        });

        // 2. Название города - null
        assertThrows(IllegalArgumentException.class, () -> {
            weatherClient.getWeatherByCity(null);
        });

        // 3. Название города содержит только пробелы
        assertThrows(IllegalArgumentException.class, () -> {
            weatherClient.getWeatherByCity("   ");
        });
    }

    @Test
    void getWeatherByCity_ShouldThrowWeatherServiceException_WhenApiError() {
        // 1. Ошибка 404 - город не найден
        stubFor(get(urlPathEqualTo("/data/2.5/weather/"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("""
                        {
                            "code": "404",
                            "message": "city not found"
                        }
                        """)));

        assertThrows(WeatherServiceException.class, () -> {
            weatherClient.getWeatherByCity("UnknownCity");
        });

        // 2. Ошибка 401 - неверный API ключ
        stubFor(get(urlPathEqualTo("/data/2.5/weather/"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("""
                        {
                            "code": 401,
                            "message": "Invalid API key"
                        }
                        """)));

        assertThrows(WeatherServiceException.class, () -> {
            weatherClient.getWeatherByCity("Paris");
        });

        // 3. Ошибка 500 - серверная ошибка
        stubFor(get(urlPathEqualTo("/data/2.5/weather/"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Server error")));

        assertThrows(WeatherServiceException.class, () -> {
            weatherClient.getWeatherByCity("London");
        });
    }

    @Test
    void getWeatherByCity_ShouldThrowWeatherServiceException_WhenConnectionFails() {
        wireMockServer.stop();

        assertThrows(WeatherServiceException.class, () -> {
            weatherClient.getWeatherByCity("Berlin");
        });
    }
}

