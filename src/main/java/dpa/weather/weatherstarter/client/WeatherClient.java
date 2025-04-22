package dpa.weather.weatherstarter.client;

import dpa.weather.weatherstarter.exception.WeatherServiceException;
import dpa.weather.weatherstarter.response.WeatherResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import java.net.URI;

public interface WeatherClient {
    WeatherResponse getWeatherByCity(@NonNull String city) throws WeatherServiceException;

    URI buildWeatherUrl(String city);

    ResponseEntity<WeatherResponse> executeWeatherRequest(URI url);
}