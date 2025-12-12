package iuh.fit.se.services.springai.impl;

import iuh.fit.se.dtos.response.RagProductResponse;
import iuh.fit.se.dtos.response.RagResponse;
import iuh.fit.se.repositories.ProductRepository;
import iuh.fit.se.services.springai.EmbeddingService;
import iuh.fit.se.services.springai.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {
    private final EmbeddingService embeddingService;
    private final ChatClient client;
    private final ProductRepository productRepository;

    @Override
    public RagResponse ask(String question) {
        // 1. Search vector store
        List<Document> docs = embeddingService.search(question);

        // 2. Build context
        String context = docs.stream()
                .map(d -> "ID=" + d.getId() + " - " + d.getText() + " (Tên: " + d.getMetadata().get("name") + ")")
                .collect(Collectors.joining("\n"));

        String prompt = """
                Bạn là chatbot của nhà hàng.
                Dựa vào thông tin món ăn bên dưới, hãy trả lời câu hỏi của khách.

                DANH SÁCH MÓN (bao gồm ID và tên):
                %s

                YÊU CẦU:
                - Trả lời tự nhiên, thân thiện
                - Chỉ dựa vào danh sách món ăn, không bịa thêm
                - Chỉ trả lời tên món ăn
                - Cuối cùng, hãy in ra danh sách ID của món liên quan theo định dạng:
                  IDS=[id1,id2,id3]

                Ví dụ: IDS=[p1,p3] (bắt buộc phải trả về danh sách IDS này cho các món bạn đã trả lời hoặc đã đề cập)

                CÂU HỎI: %s

                Nếu không có món phù hợp, hãy nói lịch sự rằng nhà hàng không có món đó
                và trả về IDS=[].
                """.formatted(context, question);


        // 3. Prompting
        String llmResponse = client.prompt()
                .user(prompt)
                .call()
                .content();

        // 4. Extract ids
        List<Long> productIds = extractIds(llmResponse);

        // 5. Query DB to get product info
        List<RagProductResponse> products = productRepository.findAllById(productIds)
                .stream()
                .map(p -> RagProductResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .indexImage(p.getIndexImage())
                        .price(p.getPrice())
                        .build())
                .collect(Collectors.toList());

        // 6. Return response
        return RagResponse.builder()
                .answer(llmResponse)
                .responseList(products)
                .build();
    }

    private List<Long> extractIds(String text) {
        // Example target: IDS=[1,2,5]
        Pattern pattern = Pattern.compile("IDS=\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) return List.of();

        String idsString = matcher.group(1).trim(); // "1,2,5"
        if (idsString.isEmpty()) return List.of();

        return Arrays.stream(idsString.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

}
