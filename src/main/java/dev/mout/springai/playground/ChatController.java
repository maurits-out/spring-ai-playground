package dev.mout.springai.playground;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * 9. Conversational History.
 */
@RestController
final class ChatController {

    private final ChatClient chatClient;
    private final String conversationId;

    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory, @Value("0") String conversationId) {
        Advisor advisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        this.chatClient = builder.defaultAdvisors(advisor).build();
        this.conversationId = conversationId;
    }

    @GetMapping("/chat")
    public String home(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
