package dev.mout.springai.playground;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 4. Stuffing the prompt.
 */
@RestController
@RequestMapping("/olympics")
final class OlympicController {

    private final ChatClient chatClient;
    private final Resource olympicSportsResource;
    private final Resource docsToStuffResource;

    public OlympicController(
            ChatClient.Builder builder,
            @Value("classpath:/prompts/olympic-sports.st") Resource olympicSportsResource,
            @Value("classpath:/docs/olympic-sports.txt") Resource docsToStuffResource) {
        this.chatClient = builder.build();
        this.olympicSportsResource= olympicSportsResource;
        this.docsToStuffResource = docsToStuffResource;
    }

    @GetMapping("/2028")
    public String get2028OlympicsSports(
            @RequestParam(value = "message", defaultValue = "What sports are being included in the 2028 Summer Olympics?") String message,
            @RequestParam(value = "stuffit", defaultValue = "false") boolean stuffit) {
        PromptTemplate template = new PromptTemplate(olympicSportsResource);
        Map<String, Object> additionalVariables = additionalVariables(message, stuffit);
        Prompt prompt = template.create(additionalVariables);
        return chatClient.prompt(prompt).call().content();
    }

    private @NonNull Map<String, Object> additionalVariables(String message, boolean stuffit) {
        return Map.of(
                "question", message,
                "context", stuffit ? docsToStuffResource : ""
        );
    }
}
