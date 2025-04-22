package dpa.weather.weatherstarter.service.implementation;

import dpa.weather.weatherstarter.client.implementation.WeatherClientImpl;
import dpa.weather.weatherstarter.response.WeatherResponse;
import dpa.weather.weatherstarter.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherClientImpl weatherClientImpl;

    @Override
    public WeatherResponse getWeatherForCity(String city) {
        return weatherClientImpl.getWeatherByCity(city);
    }
}