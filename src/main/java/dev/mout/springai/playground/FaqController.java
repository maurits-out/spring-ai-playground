package dev.mout.springai.playground;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStoreRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 5. RAG
 */
@RestController
final class FaqController {

    private final ChatClient chatClient;
    private final VectorStoreRetriever vectorStoreRetriever;
    private final Resource templateResource;

    public FaqController(ChatClient.Builder builder,
                         VectorStoreRetriever vectorStoreRetriever,
                         @Value("classpath:/prompts/rag-prompt-template.st") Resource templateResource) {
        this.chatClient = builder.build();
        this.vectorStoreRetriever = vectorStoreRetriever;
        this.templateResource = templateResource;
    }

    @GetMapping("/faq")
    public String prompt(@RequestParam(value = "message", defaultValue = "How can I buy tickets for the Olympic Games Paris 2024") String message) {
        return chatClient.prompt()
                .user(spec -> {
                    SearchRequest request = buildSearchRequest(message);
                    List<Document> documents = vectorStoreRetriever.similaritySearch(request);
                    Map<String, Object> variables = additionalVariables(message, documents);
                    spec.text(templateResource).params(variables);
                })
                .call()
                .content();
    }

    private @NonNull Map<String, Object> additionalVariables(String message, List<Document> documents) {
        List<@Nullable String> contentList = documents.stream().map(Document::getText).toList();
        return Map.of(
                "input", message,
                "documents", String.join("\n", contentList)
        );
    }

    private @NonNull SearchRequest buildSearchRequest(String message) {
        return SearchRequest.builder().query(message).topK(1).build();
    }
}
