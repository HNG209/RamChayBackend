package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.CategoryCreationRequest;
import iuh.fit.se.dtos.response.CategoryCreationResponse;
import iuh.fit.se.entities.Category;
import iuh.fit.se.mappers.CategoryMapper;
import iuh.fit.se.repositories.CategoryRepository;
import iuh.fit.se.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @PreAuthorize("hasAuthority('ADD_CATEGORY')")
    public CategoryCreationResponse createCategory(CategoryCreationRequest request) {
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        return categoryMapper.toCategoryCreationResponse(
                categoryRepository.save(category)
        );
    }
}
