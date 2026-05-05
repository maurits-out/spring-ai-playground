package dev.mout.spring_ai_demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/synonyms.st")
    private Resource synonymPromptResource;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/hello-llm")
    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String generation = chatClient.prompt(message).call().content();
        return Map.of("generation", requireNonNull(generation));
    }

    @GetMapping("/synonyms")
    public String synonyms(@RequestParam(value = "word", defaultValue = "eating") String word) {
        PromptTemplate template = new PromptTemplate(synonymPromptResource);
        Prompt prompt = template.create(Map.of("word", word));

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        requireNonNull(response);
        requireNonNull(response.getResult());

        return response.getResult().getOutput().getText();
    }

    @GetMapping("/books")
    public List<String> getBooksByAuthor(@RequestParam(value = "author", defaultValue = "Dan Brown") String author) {
        var message = """
                Give me a list of top 5 books of author {author}. If you don't know the answer, just say "I don't know".
                {format}
                """;
        ListOutputConverter converter = new ListOutputConverter();
        PromptTemplate template = new PromptTemplate(message);
        Map<String, Object> additionalVariables = Map.of(
                "author", author,
                "format", converter.getFormat()
        );
        Prompt prompt = template.create(additionalVariables);
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        requireNonNull(response);
        requireNonNull(response.getResult());
        requireNonNull(response.getResult().getOutput().getText());

        return converter.convert(response.getResult().getOutput().getText());
    }

    @GetMapping("/artist/{artist}")
    public Map<String, Object> getArtistsSocialLinks(@PathVariable("artist") String artist) {
        var message = """
                Generate a list of links for the artist {artist}. Include the name of the artist as the key and any social network links as the object.
                {format}
                """;
        MapOutputConverter converter = new MapOutputConverter();
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
