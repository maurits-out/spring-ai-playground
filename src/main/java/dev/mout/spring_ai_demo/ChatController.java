package dev.mout.spring_ai_demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/by-artist")
    public Artist getSongsByArtist(@RequestParam(value = "artist", defaultValue = "Earth, wind and fire") String artist) {
        String message = """
                Generate a list of songs performed by the artist {artist}. If you aren't positive that a song
                belongs to this artist then don't include it.
                {format}
                """;
        BeanOutputConverter<Artist> converter = new BeanOutputConverter<>(Artist.class);
        PromptTemplate template = new PromptTemplate(message);
        Map<String, Object> additionalVariables = Map.of(
                "artist", artist,
                "format", converter.getFormat()
        );
        Prompt prompt = template.create(additionalVariables);
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        requireNonNull(response);
        requireNonNull(response.getResult());
        requireNonNull(response.getResult().getOutput().getText());

        return converter.convert(response.getResult().getOutput().getText());
    }
}
