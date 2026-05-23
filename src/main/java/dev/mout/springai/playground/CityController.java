package dev.mout.springai.playground;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dev.mout.springai.playground.tools.WeatherTools.CURRENT_WEATHER_TOOL;

/**
 * 6. Using tools.
 * http :8080/cities message=="What is the weather currently in Haarlem?"
 */
@RestController
final class CityController {

    private static final String SYSTEM_MESSAGE = "You are a helpful AI assistant answering questions about cities around the world";

    private final ChatClient chatClient;

    public CityController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_MESSAGE)
                .build();
    }

    @GetMapping("/cities")
    public String cities(@RequestParam(value = "message") String message) {
        return chatClient
                .prompt()
                .user(message)
                .toolNames(CURRENT_WEATHER_TOOL)
                .call()
                .content();
    }
}
