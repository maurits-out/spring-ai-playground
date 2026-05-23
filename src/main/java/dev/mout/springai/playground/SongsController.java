package dev.mout.springai.playground;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 3c. Converting the output to a list of records.
 */
@RestController
final class SongsController {

    private static final String MESSAGE = """
            Given artists {artists}. For each artist generate a list of 3 songs performed by that artists. If you aren't positive that a song
            belongs to this artist then don't include it.""";

    private final ChatClient chatClient;

    public SongsController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/songs-by-artists")
    public List<Artist> getSongsByArtist(@RequestParam(value = "artists", defaultValue = "U2, Paul Carrack, Mike and the mechanics, Genesis") String artist) {
        return chatClient.prompt()
                .user(spec -> spec.text(MESSAGE).params(additionalVariables(artist)))
                .call()
                .entity(new ParameterizedTypeReference<>() {
                });
    }

    private @NonNull Map<String, Object> additionalVariables(String artists) {
        return Map.of("artists", artists);
    }

    public record Artist(String artist, List<String> songs) {
    }
}
