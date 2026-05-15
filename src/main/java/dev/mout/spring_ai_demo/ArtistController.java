package dev.mout.spring_ai_demo;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * 3b. Converting the output to a map.
 */
@RestController
final class ArtistController {

    private final ChatClient chatClient;
    private final MapOutputConverter converter = new MapOutputConverter();
    private final String message = """
            Generate a list of links for the artist {artist}. Include the name of the artist as the key and any social network links as the object.
            {format}
            """;

    public ArtistController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/links")
    public Map<String, Object> getSocialLinksByArtist(@RequestParam(value = "artist", defaultValue = "U2") String artist) {
        PromptTemplate template = new PromptTemplate(message);
        Prompt prompt = template.create(additionalVariables(artist));
        String content = chatClient.prompt(prompt).call().content();
        return converter.convert(Objects.requireNonNull(content));
    }

    private @NonNull Map<String, Object> additionalVariables(String artist) {
        return Map.of(
                "artist", artist,
                "format", converter.getFormat()
        );
    }
}
