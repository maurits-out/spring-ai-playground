package dev.mout.spring_ai_demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("weather")
public record WeatherConfigProperties(String apiKey, String apiUrl) {
}
