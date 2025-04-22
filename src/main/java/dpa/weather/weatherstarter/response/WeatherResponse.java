package dpa.weather.weatherstarter.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
    private String city;
    private String country;
    private Double temperature;
    private Double feelsLike;
    private String description;
    private Integer humidity;
    private Double windSpeed;
    private Long sunrise;
    private Long sunset;

    public WeatherResponse(String city, double temperature, String description) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
    }

    @JsonProperty("weather")
    private void unpackWeather(List<Weather> weather) {
        this.description = weather.get(0).getDescription();
    }

    @JsonProperty("main")
    private void unpackMain(Main main) {
        this.temperature = main.getTemp();
        this.feelsLike = main.getFeelsLike();
        this.humidity = main.getHumidity();
    }

    @JsonProperty("wind")
    private void unpackWind(Wind wind) {
        this.windSpeed = wind.getSpeed();
    }

    @JsonProperty("sys")
    private void unpackSys(Sys sys) {
        this.country = sys.getCountry();
        this.sunrise = sys.getSunrise();
        this.sunset = sys.getSunset();
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Weather {
        private String description;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Main {
        private Double temp;
        private Double feelsLike;
        private Integer humidity;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Wind {
        private Double speed;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Sys {
        private String country;
        private Long sunrise;
        private Long sunset;
    }
}