package dev.mout.springai.playground;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Media media = Media.builder()
                .data(imageResource)
                .mimeType(MimeType.valueOf(MimeTypeUtils.IMAGE_JPEG_VALUE))
                .build();
        UserMessage message = UserMessage.builder()
                .text("Describe what you see in the supplied image.")
                .media(media)
                .build();
        Prompt prompt = new Prompt(message);
        return chatClient.prompt(prompt).call().content();
    }
}
