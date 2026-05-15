package dev.mout.spring_ai_demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * 1. Simple prompt
 */
@RestController
final class HelloLLMController {

    private final ChatClient chatClient;

    public HelloLLMController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/hello-llm")
    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String generation = chatClient.prompt(message).call().content();
        return Map.of("generation", requireNonNull(generation));
    }
}
