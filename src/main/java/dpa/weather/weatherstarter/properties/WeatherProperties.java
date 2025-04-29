package dpa.weather.weatherstarter.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather")
@RequiredArgsConstructor
@Getter
@Setter
public class WeatherProperties {
    private String apiUrl;
    private String apiKey;
}