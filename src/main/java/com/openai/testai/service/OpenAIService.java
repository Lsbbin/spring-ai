package com.openai.testai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chroma.ChromaApi;
import org.springframework.ai.document.Document;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("classpath:templates/prompt/capital-prompt.st")
    private Resource capitalPrompt;

    @Value("classpath:templates/prompt/footballClub-prompt.st")
    private Resource footballClubPrompt;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    @Value("${spring.ai.openai.chat.options.max-tokens}")
    private Integer maxTokens;

    @Autowired
    private ImageModel imageModel;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChromaApi chromaApi;

    public String getCapital(String param) {
        PromptTemplate promptTemplate = new PromptTemplate(capitalPrompt);
        Prompt prompt = promptTemplate.create(Map.of("param", param));

        return this.getResponse(prompt, 0.3, 0.8);
    }


    public String getFootballClub(String param) {
        PromptTemplate promptTemplate = new PromptTemplate(footballClubPrompt);
        Prompt prompt = promptTemplate.create(Map.of("param", param));

        return this.getResponse(prompt, 0.3, 0.8);
    }


    public String getChatResponse(String param) {
        Prompt prompt = new Prompt(param);

        return this.getResponse(prompt, 0.8, 0.3);
    }


    private OpenAiChatOptions makeOption(Double temperature, Double TopP) {
        return OpenAiChatOptions.builder()
                .withModel(model) // 사용 모델
                .withTemperature(temperature) // 결과 값의 다양성 수치 (0 ~ 1) 채팅의 경우 높이고 기술번역을 하는 경우에는 낮추는게 좋아보인다.
                .withTopP(TopP) // 결과 값의 단어 사용의 일관성 수치 (0 ~ 1) 낮을수록 다양성이 높아지며, 결과 값에서 사용되는 텍스트가 다양해짐 높을수록 일관성이 높아지며, 결과 값에서 사용되는 텍스트가 덜 다양해짐
                .withMaxTokens(maxTokens) // 결과 값의 최대 토큰 수 (만약 결과 값의 Token이 해당 값보다 클 경우, 해당 값만큼의 Token만 출력)
                .build();
    }


    private String getResponse(Prompt prompt, Double temperature, Double TopP) {
        ChatResponse call = chatClient.prompt(prompt)
                .options(this.makeOption(temperature, TopP))
                .call()
                .chatResponse();

        System.out.println("Token used : " + call.getMetadata().getUsage().getTotalTokens());
        return call.getResult().getOutput().getContent();
    }


    public String getImageUrl(String param) {
        ImageResponse response = imageModel.call(
                new ImagePrompt(param,
                        OpenAiImageOptions.builder()
                                .withQuality("standard") // standard, hd
                                .withN(1)
                                .withHeight(1024)
                                .withWidth(1024)
                                .build()));

        return response.getResult().getOutput().getUrl();
    }

    public String imageAnalysis(MultipartFile file) throws IOException {
        String filePath = "C:/Users/peaku/Desktop/sample/temp/" + file.getOriginalFilename();
        if (!file.isEmpty()) file.transferTo(new File(filePath));

        String response = chatClient.prompt()
                .user(userSpec -> {
                    userSpec.text(
                                    """
                                            무슨 내용인지 분석해줘
                                            """)
                            .media(MimeTypeUtils.IMAGE_PNG, new FileSystemResource("C:/Users/peaku/Desktop/sample/temp/" + file.getOriginalFilename()));
                })
                .call()
                .content();

        try {
            Files.delete(Path.of(filePath));
        } catch (NoSuchFileException e) {
            System.out.println("There is no file/directory you want to delete");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public void createDocument(MultipartFile file) throws IOException {
        String filePath = "C:/Users/peaku/Desktop/sample/temp/" + file.getOriginalFilename();
        if (!file.isEmpty()) file.transferTo(new File(filePath));

        TextReader textReader = new TextReader(new FileSystemResource("C:/Users/peaku/Desktop/sample/temp/" + file.getOriginalFilename()));
        //List<Document> splitDocuments = TokenTextSplitter.builder().build().split(textReader.read());
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> splitDocuments = tokenTextSplitter.split(textReader.read());
        vectorStore.add(splitDocuments);

        try {
            Files.delete(Path.of(filePath));
        } catch (NoSuchFileException e) {
            System.out.println("There is no file/directory you want to delete");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCollection() {
        try {
            chromaApi.deleteCollection("SpringAiCollection");
            chromaApi.createCollection(new ChromaApi.CreateCollectionRequest("SpringAiCollection"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final ChatMemory chatMemory;

    public OpenAIService () {
        chatMemory = new InMemoryChatMemory();
    }

    public String searchDocument(String param, String id) {
        String response = chatClient.prompt()
                .advisors(
                        new MessageChatMemoryAdvisor(chatMemory, id, 100),
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults())
                )
                .user(param)
                .system("You are a customer chat support representative for a building called \"퍼블릭 가산\"When booking a visit in a friendly, helpful, and pleasant way," +
                        "your name,date and time of visit will be required. We do not encourage visitors to book until the customer first expresses their intention to visit. You will only answer questions about the context provided." +
                        "If you know the customer's name, always answer with the customer's name.")
                // 귀하는 "퍼블릭 가산"라는 건물의 고객 채팅 지원 담당자입니다. 친절하고 도움이 되며 즐거운 방식으로 방문을 예약할 때는 이름, 날짜 및 시간이 필요합니다. 고객이 먼저 방문 의사를 밝히기 전까지는 방문객에게 예약을 권장하지 않습니다.
                // 제공된 컨텍스트에 대한 질문에만 답변할 수 있습니다. 고객의 이름을 알고 있다면 항상 고객의 이름과 함께 답변하세요.
                .call()
                .content();

        return response;
    }

}
