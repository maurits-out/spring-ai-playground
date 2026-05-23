package dev.mout.springai.playground;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 3a. Converting the output to a list.
 */
@RestController
final class BooksController {

    private static final String MESSAGE = """
            Give me a list of top 5 books of artist {artist}. If you don't know the answer, just say "I don't know".
            {format}
            """;

    private final ChatClient chatClient;
    private final ListOutputConverter converter = new ListOutputConverter();

    public BooksController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/books")
    public List<String> getBooksByAuthor(@RequestParam(value = "artist", defaultValue = "Dan Brown") String author) {
        String content = chatClient.prompt()
                .user(spec -> spec.text(MESSAGE).params(additionalVariables(author)))
                .call()
                .content();
        return converter.convert(Objects.requireNonNull(content));
    }

    private @NonNull Map<String, Object> additionalVariables(String author) {
        return Map.of(
                "artist", author,
                "format", converter.getFormat()
        );
    }
}
