package iuh.fit.se.configs;

import io.pinecone.clients.Pinecone;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Value("${spring.ai.vectorstore.pinecone.api-key}")
    private String apiKey;

    @Bean
    public ChatClient chatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    public Pinecone pineconeClient() {
        // Khởi tạo Client mới nhất của Pinecone
        return new Pinecone.Builder(apiKey).build();
    }
}

