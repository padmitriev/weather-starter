package dpa.weather.weatherstarter.service;

import dpa.weather.weatherstarter.response.WeatherResponse;

public interface WeatherService {
    WeatherResponse getWeatherForCity(String city);
}