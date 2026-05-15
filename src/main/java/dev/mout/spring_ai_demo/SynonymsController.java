package dev.mout.spring_ai_demo;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
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
        PromptTemplate template = new PromptTemplate(resource);
        Prompt prompt = template.create(getAdditionalVariables(word));
        return chatClient.prompt(prompt).call().content();
    }

    private @NonNull Map<String, Object> getAdditionalVariables(String word) {
        return Map.of("word", word);
    }
}
