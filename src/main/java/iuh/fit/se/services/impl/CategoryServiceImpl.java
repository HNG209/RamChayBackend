package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.CategoryCreationRequest;
import iuh.fit.se.dtos.response.CategoryCreationResponse;
import iuh.fit.se.dtos.response.PageResponse;
import iuh.fit.se.entities.Category;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.CategoryMapper;
import iuh.fit.se.repositories.CategoryRepository;
import iuh.fit.se.repositories.ProductRepository;
import iuh.fit.se.services.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADD_CATEGORY')")
    public CategoryCreationResponse createCategory(CategoryCreationRequest request) {
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        return categoryMapper.toCategoryCreationResponse(
                categoryRepository.save(category)
        );
    }

    @Override
    public List<CategoryCreationResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryCreationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('DELETE_CATEGORY')")
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        if (productRepository.existsByCategoryId(id)) {
            throw new AppException(ErrorCode.CATEGORY_PRODUCT_EXIST);
        }

        categoryRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasAuthority('PAGE_MANAGER')")
    public PageResponse<CategoryCreationResponse> getCategoriesWithPagination(int page, int size, String keyword) {
        // 1. Tạo Pageable (Sắp xếp theo ID giảm dần)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        Page<Category> categoryPage;

        // 2. Gọi Repository
        if (keyword != null && !keyword.trim().isEmpty()) {
            categoryPage = categoryRepository.findByCategoryNameContainingIgnoreCase(keyword, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }

        // 3. Map Entity sang DTO
        List<CategoryCreationResponse> dtos = categoryPage.getContent().stream()
                .map(categoryMapper::toCategoryCreationResponse) // Hoặc map thủ công nếu chưa có mapper
                .collect(Collectors.toList());

        // 4. Đóng gói kết quả
        return PageResponse.<CategoryCreationResponse>builder()
                .content(dtos)
                .currentPage(page)
                .pageSize(categoryPage.getSize())
                .totalPages(categoryPage.getTotalPages())
                .totalElements(categoryPage.getTotalElements())
                .build();
    }

}
