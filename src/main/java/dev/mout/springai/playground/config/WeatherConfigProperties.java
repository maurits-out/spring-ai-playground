package dev.mout.springai.playground.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("weather")
public record WeatherConfigProperties(String apiKey, String apiUrl) {
}
