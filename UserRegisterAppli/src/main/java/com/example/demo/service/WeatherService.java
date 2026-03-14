package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException; // Import RestClientException
import java.util.Map;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();

    // @Value("${weather.api.key}")
    private String apiKey = "44e71fd78c866dcdf957a96a8f1d9b66";

    // @Cacheable("weather")
    public String getWeatherByCity(String city) throws InterruptedException {
        System.out.println("StartedFetching....................");
        Thread.sleep(5000);

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + this.apiKey + "&units=metric";
        // Or use 'imperial' for Fahrenheit

        try {
            ResponseEntity<Map> response = this.restTemplate.getForEntity(url, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return "Failed to get weather: " + response.getStatusCode();
            }
            System.out.println(response + "================================================================================");
            Map body = response.getBody();
            if (body == null || body.isEmpty()) {
                return "Empty weather data received.";
            }

            // 1. Get main temp
            Map main = (Map) body.get("main");
            // 2. Get weather description
            Map weather = ((java.util.List<Map>) body.get("weather")).get(0);
            // 3. Get City Name
            String cityName = (String) body.get("name");
            // 4. Get Country/State
            Map sys = (Map) body.get("sys");
            String country = (String) sys.get("country");

            double temp = (Double) main.get("temp");
            String description = (String) weather.get("description");

            // Return formatted string with City, Country, Temp, and Description
            return "Current weather in " + cityName + ", " + country + " : " + temp + "°C, " + description;

        } catch (RestClientException e) { // Catches exceptions related to the API call
            // Log the exception for debugging purposes if needed
            e.printStackTrace(); 
            return "An error occurred while fetching weather data: " + e.getMessage();
        }
    }
}
