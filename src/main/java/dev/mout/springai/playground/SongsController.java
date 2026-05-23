package dev.mout.springai.playground;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 3c. Converting the output to a bean.
 */
@RestController
final class SongsController {

    private static final String MESSAGE = """
            Generate a list of songs performed by the artist {artist}. If you aren't positive that a song
            belongs to this artist then don't include it.""";

    private final ChatClient chatClient;

    public SongsController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/songs-by-artist")
    public Artist getSongsByArtist(@RequestParam(value = "artist", defaultValue = "Earth, wind and fire") String artist) {
        PromptTemplate template = new PromptTemplate(MESSAGE);
        Prompt prompt = template.create(additionalVariables(artist));
        return chatClient.prompt(prompt).call().entity(Artist.class);
    }

    private @NonNull Map<String, Object> additionalVariables(String artist) {
        return Map.of("artist", artist);
    }

    public record Artist(String artist, List<String> songs) {
    }
}
