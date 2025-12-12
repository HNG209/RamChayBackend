package iuh.fit.se.services.springai;

import iuh.fit.se.dtos.request.ProductEmbeddingRequest;
import org.springframework.ai.document.Document;

import java.util.List;

public interface EmbeddingService {
    void createEmbedding(ProductEmbeddingRequest product);
    List<Document> search(String query);
}
