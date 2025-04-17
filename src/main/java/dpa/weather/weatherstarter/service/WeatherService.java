package dpa.weather.weatherstarter.service;

import dpa.weather.weatherstarter.responce.WeatherResponse;

public interface WeatherService {
    WeatherResponse getWeatherForCity(String city);
}
