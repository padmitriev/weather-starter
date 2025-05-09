package dpa.weather.weatherstarter.client.implementation;

import dpa.weather.weatherstarter.client.WeatherClient;
import dpa.weather.weatherstarter.exception.WeatherServiceException;
import dpa.weather.weatherstarter.properties.WeatherProperties;
import dpa.weather.weatherstarter.response.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherClientImpl implements WeatherClient {

    private final RestTemplate restTemplate;
    private final WeatherProperties weatherProperties;

    public WeatherResponse getWeatherByCity(@NonNull String city) throws WeatherServiceException {
        validateCityName(city);

        URI url = buildWeatherUrl(city);
        log.debug("Requesting weather data for {} from URL: {}", city, url);

        try {
            ResponseEntity<WeatherResponse> response = executeWeatherRequest(url);
            validateResponse(response);

            WeatherResponse weatherResponse = response.getBody();
            if (weatherResponse != null) {
                weatherResponse.setCity(city);
            }
            return weatherResponse;

        } catch (RestClientResponseException e) {
            String errorMsg = String.format("API error for city '%s'. Status: %d. Response: %s",
                    city,
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString());
            log.error(errorMsg, e);
            throw new WeatherServiceException(errorMsg, e);

        } catch (ResourceAccessException e) {
            String errorMsg = String.format("Connection failed for city '%s'. URL: %s. Error: %s",
                    city,
                    url,
                    e.getMessage());
            log.error(errorMsg, e);
            throw new WeatherServiceException(errorMsg, e);

        } catch (RestClientException e) {
            String errorMsg = String.format("Unexpected API error for city '%s': %s",
                    city,
                    e.getMessage());
            log.error(errorMsg, e);
            throw new WeatherServiceException(errorMsg, e);
        }
    }

    public URI buildWeatherUrl(String city) {
        try {
            return new URI(weatherProperties.getApiUrl())
                    .resolve(UriComponentsBuilder.newInstance()
                            .queryParam("q", city)
                            .queryParam("appid", weatherProperties.getApiKey())
                            .build()
                            .toUriString());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid API URL configuration", e);
        }
    }

    @Override
    public ResponseEntity<WeatherResponse> executeWeatherRequest(URI url) {
        log.debug("Sending request to URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                WeatherResponse.class
        );
    }

    private void validateCityName(String city) {
        if (city.isBlank()) {
            throw new IllegalArgumentException("City name cannot be null or empty");
        }
    }

    private void validateResponse(ResponseEntity<WeatherResponse> response) {
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new WeatherServiceException("Invalid API response status: " + response.getStatusCode());
        }
        if (response.getBody() == null) {
            throw new WeatherServiceException("Empty response body from weather API");
        }
    }
}