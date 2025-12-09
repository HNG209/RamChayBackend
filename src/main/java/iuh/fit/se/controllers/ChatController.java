package iuh.fit.se.controllers;

import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.RagResponse;
import iuh.fit.se.services.springai.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatController {
    private final RagService ragService;

    @GetMapping("/ask")
    public ApiResponse<RagResponse> ask(@RequestParam String q) {
        return ApiResponse.<RagResponse>builder()
                .result(ragService.ask(q))
                .build();
    }
}
