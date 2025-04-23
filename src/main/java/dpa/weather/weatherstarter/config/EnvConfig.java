package dpa.weather.weatherstarter.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:.env", ignoreResourceNotFound = true)
public class EnvConfig {

    @Value("${WEATHER_API_KEY}")
    private String weatherApiKey;

    @PostConstruct
    public void loadEnv() {
        System.setProperty("WEATHER_API_KEY", weatherApiKey);
    }
}