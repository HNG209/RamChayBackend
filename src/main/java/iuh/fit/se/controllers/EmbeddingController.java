package iuh.fit.se.controllers;

import iuh.fit.se.dtos.request.ProductEmbeddingRequest;
import iuh.fit.se.dtos.response.RagResponse;
import iuh.fit.se.services.springai.EmbeddingService;
import iuh.fit.se.services.springai.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/embedding")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EmbeddingController {
    private final EmbeddingService embeddingService;
//    @PostMapping(name = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public String image(@RequestPart MultipartFile file) {
//        embeddingService.embedImage(file);
//        return "2048";
//    }
    @GetMapping("/search")
    public List<Document> search(@RequestParam String q) {
        return embeddingService.search(q);
    }
}
