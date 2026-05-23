package dev.mout.springai.playground;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG;

/**
 * 7. Describing image
 */
@RestController
final class ImageController {

    private final ChatClient chatClient;

    private final Resource imageResource;

    public ImageController(ChatClient.Builder builder, @Value("classpath:/images/image.jpeg") Resource imageResource) {
        this.chatClient = builder.build();
        this.imageResource = imageResource;
    }

    @GetMapping("/describe-image")
    public String describeImage() {
        return chatClient.prompt()
                .user(spec -> spec
                        .text("Describe what you see in the supplied image.")
                        .media(IMAGE_JPEG, imageResource))
                .call()
                .content();
    }
}
