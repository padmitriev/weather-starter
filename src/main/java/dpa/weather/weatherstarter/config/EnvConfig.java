package dpa.weather.weatherstarter.config;

import dpa.weather.weatherstarter.properties.WeatherProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(WeatherProperties.class)
@PropertySource(value = "classpath:.env", ignoreResourceNotFound = true)
public class EnvConfig {
}