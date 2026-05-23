package dev.mout.springai.playground;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * 8. Return a stream.
 */
@RestController
final class BestRestaurantsController {

    private final ChatClient chatClient;
    private final int bufferSize;

    public BestRestaurantsController(ChatClient.Builder builder, @Value("50") int bufferSize) {
        this.chatClient = builder.build();
        this.bufferSize = bufferSize;
    }

    @GetMapping(path = "/best-restaurants", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getBestRestaurants(@RequestParam("city") String city) {
        return chatClient.prompt()
                .system("""
                    You are given a name of a city. Provide a list of the 10
                    best restaurants in that city together with the address and
                    short description.""")
                .user(spec -> spec.text(city))
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
