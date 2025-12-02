package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.ProductCreationRequest;
import iuh.fit.se.dtos.response.MediaUploadResponse;
import iuh.fit.se.dtos.response.ProductCreationResponse;
import iuh.fit.se.entities.Category;
import iuh.fit.se.entities.Media;
import iuh.fit.se.entities.Product;
import iuh.fit.se.mappers.ProductMapper;
import iuh.fit.se.repositories.CategoryRepository;
import iuh.fit.se.repositories.MediaRepository;
import iuh.fit.se.repositories.ProductRepository;
import iuh.fit.se.services.ProductService;
import iuh.fit.se.services.cloud.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MediaRepository mediaRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADD_PRODUCT')")
    public ProductCreationResponse createProduct(ProductCreationRequest productCreationRequest, List<MultipartFile> images) throws IOException {

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

        Set<Media> mediaSet = new HashSet<>();

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                MediaUploadResponse mediaUploadResponse = cloudinaryService.upload(image);

                Media mediaRequest = Media.builder()
                        .product(savedProduct)
                        .publicId(mediaUploadResponse.getPublicId())
                        .secureUrl(mediaUploadResponse.getSecureUrl())
                        .build();

                mediaSet.add(mediaRequest);
            }
        }

        mediaRepository.saveAll(mediaSet);

        return productMapper.toProductCreationResponse(savedProduct);
    }
}
