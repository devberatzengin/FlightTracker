package org.devberat.service;

public interface IWeatherService {
    String getWeatherCondition(String city);
    int getTemperature(String city);
}
