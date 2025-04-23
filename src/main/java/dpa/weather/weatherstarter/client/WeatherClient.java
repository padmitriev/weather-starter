package dpa.weather.weatherstarter.client;

import dpa.weather.weatherstarter.response.WeatherResponse;
import org.springframework.http.ResponseEntity;
import java.net.URI;

public interface WeatherClient {
    WeatherResponse getWeatherByCity(String city);

    URI buildWeatherUrl(String city);

    ResponseEntity<WeatherResponse> executeWeatherRequest(URI url);
}