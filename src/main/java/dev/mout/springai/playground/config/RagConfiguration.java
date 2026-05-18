package dev.mout.springai.playground.config;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStoreRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class RagConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RagConfiguration.class);

    @Value("classpath:/docs/olympic-faq.txt")
    private Resource faq;

    @Value("vectorstore.json")
    private String vectorStoreName;

    @Bean
    TokenTextSplitter tokenTextSplitter() {
        return TokenTextSplitter.builder().build();
    }

    @Bean
    VectorStoreRetriever vectorStoreRetriever(EmbeddingModel embeddingModel, TokenTextSplitter splitter) {
        SimpleVectorStore store = constructSimpleVectorStore(embeddingModel);
        File file = getVectorStorePath();
        if (file.exists()) {
            loadFromFile(store, file);
        } else {
            createFromDocument(store, file, splitter);
        }
        return store;
    }

    private void createFromDocument(SimpleVectorStore store, File file, TokenTextSplitter splitter) {
        log.info("Creating new Vector Store from document");
        List<Document> splitDocuments = loadAndSplitDocument(splitter);
        store.add(splitDocuments);
        store.save(file);
    }

    private @NonNull List<Document> loadAndSplitDocument(TokenTextSplitter splitter) {
        TextReader reader = new TextReader(faq);
        List<Document> documents = reader.get();
        return splitter.apply(documents);
    }

    private void loadFromFile(SimpleVectorStore store, File file) {
        log.info("Loading Vector Store from file {}", file);
        store.load(file);
    }

    private @NonNull SimpleVectorStore constructSimpleVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    private File getVectorStorePath() {
        return Paths.get(System.getProperty("user.home"), vectorStoreName).toFile();
    }
}
