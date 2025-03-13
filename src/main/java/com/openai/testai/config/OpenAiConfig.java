package com.openai.testai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chroma.ChromaApi;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.ChromaVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;


@Configuration
public class OpenAiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String openaiApiKey;

    @Value("${chroma.vector-store.url}")
    private String chromaUrl;

    @Bean
    public ChatClient chatClient() {
        ChatModel chatModel = new OpenAiChatModel(new OpenAiApi(openaiApiKey), OpenAiChatOptions.builder().withModel(OpenAiApi.DEFAULT_CHAT_MODEL).withTemperature(0.7).build());
        return ChatClient
                .builder(chatModel)
                .build();
    }

    @Bean
    public RestClient.Builder builder() {
        return RestClient.builder().requestFactory(new SimpleClientHttpRequestFactory());
    }

    @Bean
    public ChromaApi chromaApi(RestClient.Builder restClientBuilder) {
        ChromaApi chromaApi = new ChromaApi(chromaUrl, restClientBuilder);
        //chromaApi.createCollection(new ChromaApi.CreateCollectionRequest("TEST1"));
        //chromaApi.deleteCollection("SpringAiCollection");
        return chromaApi;
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return new OpenAiEmbeddingModel(
                new OpenAiApi(openaiApiKey)
                , MetadataMode.EMBED
                , OpenAiEmbeddingOptions.builder().withModel(OpenAiApi.EmbeddingModel.TEXT_EMBEDDING_3_SMALL.getValue()
        ).build());
    }

    @Bean
    public VectorStore chromaVectorStore(EmbeddingModel embeddingModel, ChromaApi chromaApi) {
        return new ChromaVectorStore.Builder(embeddingModel, chromaApi)
                .collectionName("TEST1")
                .initializeSchema(true)
                .build();
    }
}