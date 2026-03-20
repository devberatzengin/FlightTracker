package org.devberat.controller.Impl;

import lombok.RequiredArgsConstructor;
import org.devberat.model.RootEntity;
import org.devberat.service.Impl.WeatherService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rest/api/weather")
@RequiredArgsConstructor
public class WeatherController extends RestBaseController {

    private final WeatherService weatherService;

    @GetMapping("/{city}")
    public RootEntity<Map<String, Object>> getWeather(@PathVariable String city) {
        Map<String, Object> data = new HashMap<>();
        data.put("city", city);
        data.put("condition", weatherService.getWeatherCondition(city));
        data.put("temp", weatherService.getTemperature(city));
        return ok(data);
    }
}
