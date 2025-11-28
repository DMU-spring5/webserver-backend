package com.websever.websever.controller.mainpage;


import com.websever.websever.dto.response.WeatherResponse;
import com.websever.websever.service.mainpage.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public WeatherResponse getWeather() {
        return weatherService.getWeatherFixedLocation();
    }
}