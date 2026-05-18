package dev.mout.springai.playground;

import dev.mout.springai.playground.config.WeatherConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(WeatherConfigProperties.class)
@SpringBootApplication
public class SpringAiDemoApplication {

	static void main(String[] args) {
		SpringApplication.run(SpringAiDemoApplication.class, args);
	}

}
