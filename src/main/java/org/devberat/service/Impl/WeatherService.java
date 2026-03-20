package org.devberat.service.Impl;

import org.devberat.service.IWeatherService;
import org.springframework.stereotype.Service;

@Service
public class WeatherService implements IWeatherService {

    public String getWeatherCondition(String city) {
        String[] conditions = {"Sunny", "Cloudy", "Rainy", "Partly Cloudy", "Clear Sky"};
        // Deterministic random based on city name length and first char for realism
        int index = (city.length() + city.charAt(0)) % conditions.length;
        return conditions[index];
    }

    public int getTemperature(String city) {
        // Mock temperature between 15 and 30
        int base = 20;
        int offset = (city.length() * 7) % 15;
        return base + offset - 5;
    }
}
