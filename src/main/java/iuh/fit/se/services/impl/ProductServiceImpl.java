package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.ProductCreationRequest;
import iuh.fit.se.dtos.response.ProductCreationResponse;
import iuh.fit.se.entities.Category;
import iuh.fit.se.entities.Product;
import iuh.fit.se.mappers.ProductMapper;
import iuh.fit.se.repositories.CategoryRepository;
import iuh.fit.se.repositories.ProductRepository;
import iuh.fit.se.services.ProductService;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADD_PRODUCT')")
    public ProductCreationResponse createProduct(ProductCreationRequest productCreationRequest) {
        Category category = Category.builder()
                .categoryName(productCreationRequest.getCategory().getCategoryName())
                .description(productCreationRequest.getCategory().getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);

        Product product = Product.builder()
                .name(productCreationRequest.getName())
                .description(productCreationRequest.getDescription())
                .category(savedCategory)
                .price(productCreationRequest.getPrice())
                .stock(productCreationRequest.getStock())
                .build();

        Product savedProduct = productRepository.save(product);

        return productMapper.toProductCreationResponse(savedProduct);
    }
}
