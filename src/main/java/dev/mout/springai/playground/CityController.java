package dev.mout.springai.playground;

import dev.mout.springai.playground.tools.WeatherTools.Request;
import dev.mout.springai.playground.tools.WeatherTools.Response;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

import static dev.mout.springai.playground.tools.WeatherTools.CURRENT_WEATHER_TOOL;

/**
 * 6. Using tools.
 * http :8080/cities message=="What is the weather currently in Haarlem?"
 */
@RestController
final class CityController {

    private static final String SYSTEM_MESSAGE = "You are a helpful AI assistant answering questions about cities around the world";

    private final ChatClient chatClient;

    public CityController(ChatClient.Builder builder, Function<Request, Response> currentWeatherFunction) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_MESSAGE)
                .defaultTools(spec -> spec.callbacks(constructToolCallback(currentWeatherFunction)))
                .build();
    }

    @GetMapping("/cities")
    public String cities(@RequestParam(value = "message") String message) {
        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    private @NonNull FunctionToolCallback<Request, Response> constructToolCallback(Function<Request, Response> currentWeatherFunction) {
        return FunctionToolCallback
                .builder(CURRENT_WEATHER_TOOL, currentWeatherFunction)
                .inputType(Request.class)
                .description("Get the current weather conditions for the given city")
                .build();
    }
}
