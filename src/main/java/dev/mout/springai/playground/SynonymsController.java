package dev.mout.springai.playground;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 2. Create prompt from template.
 */
@RestController
final class SynonymsController {

    private final ChatClient chatClient;
    private final Resource resource;

    public SynonymsController(ChatClient.Builder builder,
                              @Value("classpath:/prompts/synonyms.st") Resource resource) {
        this.chatClient = builder.build();
        this.resource = resource;
    }

    @GetMapping("/synonyms")
    public String synonyms(@RequestParam(value = "word", defaultValue = "eating") String word) {
        return chatClient.prompt()
                .user(spec -> spec.text(resource).params(additionalVariables(word)))
                .call()
                .content();
    }

    private @NonNull Map<String, Object> additionalVariables(String word) {
        return Map.of("word", word);
    }
}
