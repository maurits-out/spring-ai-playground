package dev.mout.springai.playground;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 8. Return a stream.
 */
@RestController
final class HamburgerRestaurantsController {

    private final ChatClient chatClient;
    private final int bufferSize;

    public HamburgerRestaurantsController(ChatClient.Builder builder, @Value("50") int bufferSize) {
        this.chatClient = builder.build();
        this.bufferSize = bufferSize;
    }

    @GetMapping(path = "/hamburger-restaurants", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getHamburgerRestaurants() {
        return chatClient.prompt()
                .user(spec -> spec.text("What are the 10 best hamburger restaurants in Amsterdam. Include a short description for each restaurant."))
                .stream()
                .content()
                .transform(this::toChunk);
    }

    private Flux<String> toChunk(Flux<String> tokenFlux) {
        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();
            tokenFlux.subscribe(
                    token -> {
                        buffer.append(token);
                        if (buffer.length() >= bufferSize) {
                            sink.next(buffer.toString());
                            buffer.setLength(0);
                        }
                    },
                    sink::error,
                    () -> {
                        if (!buffer.isEmpty()) {
                            sink.next(buffer.toString());
                        }
                        sink.complete();
                    }
            );
        });
    }
}
