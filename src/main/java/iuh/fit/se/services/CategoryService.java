package iuh.fit.se.services;

import iuh.fit.se.dtos.request.CategoryCreationRequest;
import iuh.fit.se.dtos.response.CategoryCreationResponse;

import java.util.List;

public interface CategoryService {
    CategoryCreationResponse createCategory(CategoryCreationRequest request);
    List<CategoryCreationResponse> getAllCategories();
    void deleteCategory(Long id);
}
