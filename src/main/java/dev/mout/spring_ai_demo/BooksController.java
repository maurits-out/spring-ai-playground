package dev.mout.spring_ai_demo;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
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

    private final ChatClient chatClient;
    private final ListOutputConverter converter = new ListOutputConverter();
    private final String message = """
            Give me a list of top 5 books of author {author}. If you don't know the answer, just say "I don't know".
            {format}
            """;

    public BooksController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/books")
    public List<String> getBooksByAuthor(@RequestParam(value = "author", defaultValue = "Dan Brown") String author) {
        PromptTemplate template = new PromptTemplate(message);
        Prompt prompt = template.create(getAdditionalVariables(author));
        String content = chatClient.prompt(prompt).call().content();
        return converter.convert(Objects.requireNonNull(content));
    }

    private @NonNull Map<String, Object> getAdditionalVariables(String author) {
        return Map.of(
                "author", author,
                "format", converter.getFormat()
        );
    }
}
