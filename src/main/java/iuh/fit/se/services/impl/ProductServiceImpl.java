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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        String categoryName = productCreationRequest.getCategory().getCategoryName();

        Category existingCategory = categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = Product.builder()
                .name(productCreationRequest.getName())
                .description(productCreationRequest.getDescription())
                .category(existingCategory)
                .price(productCreationRequest.getPrice())
                .stock(productCreationRequest.getStock())
                .unit(productCreationRequest.getUnit())
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

    @Override
    public List<ProductCreationResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(productMapper::toProductCreationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCreationResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        return productMapper.toProductCreationResponse(product);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('DELETE_PRODUCT')")
    public void deleteProduct(Long productId) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

// Xóa ảnh trên Cloudinary (Dọn dẹp)
        Set<Media> mediaFiles = product.getMediaFiles();

        if (!mediaFiles.isEmpty()) {
            Set<Media> mediaFilesCopy = new HashSet<>(mediaFiles);
            for (Media media : mediaFilesCopy) {
                if (media.getPublicId() != null) {
                    cloudinaryService.deleteImage(media.getPublicId());
                }
            }
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('EDIT_PRODUCT')")
    public ProductCreationResponse updateProduct(Long productId, ProductCreationRequest productCreationRequest, List<MultipartFile> images) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setName(productCreationRequest.getName());
        product.setDescription(productCreationRequest.getDescription());
        product.setPrice(productCreationRequest.getPrice());
        product.setStock(productCreationRequest.getStock());
        product.setUnit(productCreationRequest.getUnit());

        Category currentCategory = product.getCategory();
        if (currentCategory != null) {
            currentCategory.setCategoryName(productCreationRequest.getCategory().getCategoryName());
            currentCategory.setDescription(productCreationRequest.getCategory().getDescription());
            categoryRepository.save(currentCategory);
        }

        List<Long> idsToDelete = productCreationRequest.getImageIdsToDelete();
        if (idsToDelete != null && !idsToDelete.isEmpty()) {
            List<Media> mediasToDelete = mediaRepository.findAllById(idsToDelete);
            for (Media media : mediasToDelete) {
                if (media.getPublicId() != null) {
                    cloudinaryService.deleteImage(media.getPublicId());
                }
                product.getMediaFiles().remove(media);
                mediaRepository.delete(media);
            }
        }

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    MediaUploadResponse res = cloudinaryService.upload(image);
                    Media newMedia = Media.builder()
                            .product(product)
                            .publicId(res.getPublicId())
                            .secureUrl(res.getSecureUrl())
                            .build();

                    product.getMediaFiles().add(newMedia);
                }
            }
            // Lưu ý: Vì CascadeType.ALL, chỉ cần save Product là Media tự lưu
            // Nhưng nếu muốn chắc ăn, có thể save mediaRepository.saveAll(...)
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductCreationResponse(savedProduct);
    }


}
