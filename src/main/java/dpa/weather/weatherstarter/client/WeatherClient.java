package dpa.weather.weatherstarter.client;

import dpa.weather.weatherstarter.response.WeatherResponse;
import org.springframework.http.ResponseEntity;
import java.net.URI;

public interface WeatherClient {
    ResponseEntity<WeatherResponse> executeWeatherRequest(URI url);
}