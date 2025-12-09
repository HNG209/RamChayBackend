package iuh.fit.se.services.springai.impl;

import iuh.fit.se.dtos.request.ProductEmbeddingRequest;
import iuh.fit.se.services.springai.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    @Override
    public void createEmbedding(ProductEmbeddingRequest product) {
        // 1. Validate dữ liệu đầu vào
        if (product.getDescription() == null || product.getName() == null) {
            throw new IllegalArgumentException("Product name and description must not be null");
        }

        System.out.println("Processing Product ID: " + product.getId());

        // 2. Kỹ thuật "Context Enrichment": Gộp thông tin để Vector hiểu rõ hơn
        // Ví dụ: "Tên: iPhone 15. Mô tả: Thiết bị di động Apple..."
        String contentToEmbed = String.format("%s. %s", product.getName(), product.getDescription());

        // 3. Tạo Document
        Document doc = new Document(
                String.valueOf(product.getId()), // QUAN TRỌNG: Dùng ID thật của Product để tránh duplicate
                contentToEmbed,
                Map.of(
                        "original_id", product.getId(),
                        "name", product.getName()
                )
        );

        // 4. Lưu vào Vector Store (Tự động Embed + Save)
        vectorStore.add(List.of(doc));
    }

    @Override
    public List<Document> search(String query) {
        return vectorStore.similaritySearch(query);
    }
//    @Override
//    public void embedImage(MultipartFile file) {
//        try {
//            // 1. Xử lý ảnh sang Base64
//            byte[] bytes = file.getBytes();
//            String base64 = Base64.getEncoder().encodeToString(bytes);
//            String dataUri = "data:image/png;base64," + base64;
//
//            float[] embeddingFloat = embeddingModel.embedForResponse(List.of(dataUri))
//                    .getResult().getOutput().clone();
//
//            List<Double> embeddingList = IntStream.range(0, embeddingFloat.length)
//                    .mapToObj(i -> (double) embeddingFloat[i])
//                    .toList();
//
//            List<Float> embeddingListFloat = embeddingList.stream()
//                    .map(Double::floatValue)
//                    .collect(Collectors.toList());
//
//            Struct metadataStruct = createMetadata(file.getOriginalFilename());
//
//            try (Index indexConnection = pinecone.getIndexConnection(indexName)) {
//                String id = UUID.randomUUID().toString();
//
//                indexConnection.upsert(
//                        id,
//                        embeddingListFloat,
//                        null,
//                        null,
//                        metadataStruct,
//                        "default"
//                );
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException("Cannot read image file", e);
//        }
//    }
//
//    // Helper method để tạo Metadata dạng Google Protobuf Struct
//    private Struct createMetadata(String fileName) {
//        return Struct.newBuilder()
//                .putFields("fileName", Value.newBuilder().setStringValue(fileName).build())
//                .putFields("source", Value.newBuilder().setStringValue("manual-upload").build())
//                .build();
//    }
//
//    @Override
//    public List<Document> search(String query) {
//        // 1. Tạo embedding cho câu query (Text)
//        float[] queryEmbedding = embeddingModel.embed(query);
//
//        // Chuyển float[] -> List<Float>
//        List<Float> queryVector = new java.util.ArrayList<>();
//        for (float v : queryEmbedding) {
//            queryVector.add(v);
//        }
//
//        try (Index indexConnection = pinecone.getIndexConnection(indexName)) {
//
//            QueryResponseWithUnsignedIndices response = indexConnection.query(
//                    10,            // 1. topK: Số lượng kết quả
//                    queryVector,        // 2. vector: Vector tìm kiếm
//                    null,               // 3. sparseIndices: Để null
//                    null,               // 4. sparseValues: Để null
//                    null,               // 5. id: Để null (vì ta search bằng vector, không phải bằng ID)
//                    "default",          // 6. namespace: "default" hoặc tên bạn đặt
//                    null,               // 7. filter: Để null (nếu không lọc theo metadata)
//                    true,               // 8. includeValues: Có lấy giá trị vector về không
//                    true                // 9. includeMetadata: Có lấy metadata về không (Cần thiết để map ra Document)
//            );
//
//            // 3. Map kết quả trả về sang List<Document> của Spring AI
//            return response.getMatchesList().stream()
//                    .map(match -> {
//                        Map<String, Object> metadata = new HashMap<>();
//                        // Extract Metadata từ Struct của Google Protobuf
//                        if (match.getMetadata() != null) {
//                            match.getMetadata().getFieldsMap().forEach((key, value) -> {
//                                metadata.put(key, value.getStringValue());
//                            });
//                        }
//
//                        return Document.builder()
//                                .id(match.getId())
//                                .text("") // Nội dung rỗng (vì là ảnh)
//                                .metadata(metadata)
//                                .score((double) match.getScore())
//                                .build();
//                    })
//                    .collect(Collectors.toList());
//        }
//    }
}