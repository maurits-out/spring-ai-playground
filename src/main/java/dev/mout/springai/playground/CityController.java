package dev.mout.springai.playground;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static dev.mout.springai.playground.tools.WeatherTools.CURRENT_WEATHER_TOOL;

/**
 * 6. Using tools.
 * http :8080/cities message=="What is the weather currently in Haarlem?"
 */
@RestController
final class CityController {

    private static final SystemMessage SYSTEM_MESSAGE = new SystemMessage("You are a helpful AI assistant answering questions about cities around the world");

    private final ChatClient chatClient;

    public CityController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/cities")
    public String cities(@RequestParam(value = "message") String message) {
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(SYSTEM_MESSAGE, userMessage));
        return chatClient
                .prompt(prompt)
                .toolNames(CURRENT_WEATHER_TOOL)
                .call()
                .content();
    }
}
