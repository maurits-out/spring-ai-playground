package dev.mout.springai.playground.tools;

import dev.mout.springai.playground.config.WeatherConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

@Configuration
public class WeatherTools {

    private static final Logger log = LoggerFactory.getLogger(WeatherTools.class);

    @Bean("currentWeather")
    public Function<Request, Response> currentWeather(WeatherConfigProperties props) {
        RestClient client = RestClient.create(props.apiUrl());
        return request -> {
            log.info("Weather Request: {}", request);
            Response response = client.get()
                    .uri("/current.json?key={key}&q={q}", props.apiKey(), request.city())
                    .retrieve()
                    .body(Response.class);
            log.info("Weather API Response: {}", response);
            return response;
        };
    }

    public record Request(String city) {
    }

    public record Response(Location location, Current current) {
    }

    public record Location(String name, String region, String country, Long lat, Long lon) {
    }

    public record Current(String temp_c, Condition condition, String wind_mph, String humidity) {
    }

    public record Condition(String text) {
    }
}
