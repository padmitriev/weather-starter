package dpa.weather.weatherstarter.config;

import dpa.weather.weatherstarter.client.implementation.WeatherClientImpl;
import dpa.weather.weatherstarter.client.WeatherClient;
import dpa.weather.weatherstarter.properties.WeatherProperties;
import dpa.weather.weatherstarter.service.WeatherService;
import dpa.weather.weatherstarter.service.implementation.WeatherServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(prefix = "weather", name = "enabled", havingValue = "true", matchIfMissing = false)
public class WeatherStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public WeatherProperties weatherProperties() {
        return new WeatherProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public WeatherClient weatherClient(RestTemplate restTemplate, WeatherProperties weatherProperties) {
        return new WeatherClientImpl(restTemplate, weatherProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public WeatherService weatherService(WeatherClient weatherClient) {
        return new WeatherServiceImpl(weatherClient);
    }
}