package iuh.fit.se.services;

import iuh.fit.se.dtos.request.CategoryCreationRequest;
import iuh.fit.se.dtos.response.CategoryCreationResponse;
import iuh.fit.se.dtos.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryCreationResponse createCategory(CategoryCreationRequest request);
    List<CategoryCreationResponse> getAllCategories();
    void deleteCategory(Long id);
    PageResponse<CategoryCreationResponse> getCategoriesWithPagination(int page, int size, String keyword);

}
