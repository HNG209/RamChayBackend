package iuh.fit.se.services.springai;

import iuh.fit.se.dtos.response.RagResponse;

public interface RagService {
    RagResponse ask(String question);
}
