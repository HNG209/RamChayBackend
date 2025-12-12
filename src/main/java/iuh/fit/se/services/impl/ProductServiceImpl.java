package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.ProductCreationRequest;
import iuh.fit.se.dtos.request.ProductEmbeddingRequest;
import iuh.fit.se.dtos.response.MediaUploadResponse;
import iuh.fit.se.dtos.response.PageResponse;
import iuh.fit.se.dtos.response.ProductCreationResponse;
import iuh.fit.se.entities.Category;
import iuh.fit.se.entities.Media;
import iuh.fit.se.entities.Product;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.ProductMapper;
import iuh.fit.se.repositories.CategoryRepository;
import iuh.fit.se.repositories.MediaRepository;
import iuh.fit.se.repositories.ProductRepository;
import iuh.fit.se.services.ProductService;
import iuh.fit.se.services.cloud.CloudinaryService;
import iuh.fit.se.services.springai.EmbeddingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final EmbeddingService embeddingService;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ADD_PRODUCT')")
    public ProductCreationResponse createProduct(ProductCreationRequest productCreationRequest, List<MultipartFile> images) throws IOException {
        if (images == null || images.isEmpty() || images.stream().allMatch(MultipartFile::isEmpty)) {
            throw new AppException(ErrorCode.PRODUCT_MEDIA_INVALID);
        }

        Long categoryId = productCreationRequest.getCategoryId();

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .name(productCreationRequest.getName())
                .description(productCreationRequest.getDescription())
                .category(existingCategory)
                .price(productCreationRequest.getPrice())
                .stock(productCreationRequest.getStock())
                .unit(productCreationRequest.getUnit())
                .indexImage("")
                .build();


        Product savedProduct = productRepository.save(product);

        Set<Media> mediaSet = new HashSet<>();
        String firstImageUrl = null;

        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            if (!image.isEmpty()) {
                MediaUploadResponse mediaUploadResponse = cloudinaryService.upload(image);

                if (firstImageUrl == null) {
                    firstImageUrl = mediaUploadResponse.getSecureUrl();
                }

                Media mediaRequest = Media.builder()
                        .product(savedProduct)
                        .publicId(mediaUploadResponse.getPublicId())
                        .secureUrl(mediaUploadResponse.getSecureUrl())
                        .build();
                mediaSet.add(mediaRequest);
            }
        }

        mediaRepository.saveAll(mediaSet);

        if (firstImageUrl != null) {
            savedProduct.setIndexImage(firstImageUrl); // <--- SỬA
            savedProduct = productRepository.save(savedProduct);
        }

        embeddingService.createEmbedding(ProductEmbeddingRequest.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .build());

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
    public List<ProductCreationResponse> findProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);

        return products.stream()
                .map(productMapper::toProductCreationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCreationResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return productMapper.toProductCreationResponse(product);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('DELETE_PRODUCT')")
    public void deleteProduct(Long productId) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

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
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        long currentImagesCount = product.getMediaFiles().size();
        List<Long> idsToDelete = productCreationRequest.getImageIdsToDelete();
        long deleteCount = 0;
        if (idsToDelete != null && !idsToDelete.isEmpty()) {
            deleteCount = product.getMediaFiles().stream()
                    .map(Media::getId)
                    .filter(idsToDelete::contains)
                    .count();
        }
        long newImagesCount = (images != null) ? images.stream().filter(img -> !img.isEmpty()).count() : 0;

        if ((currentImagesCount - deleteCount + newImagesCount) < 1) {
            throw new AppException(ErrorCode.PRODUCT_MEDIA_INVALID);
        }

        product.setName(productCreationRequest.getName());
        product.setDescription(productCreationRequest.getDescription());
        product.setPrice(productCreationRequest.getPrice());
        product.setStock(productCreationRequest.getStock());
        product.setUnit(productCreationRequest.getUnit());

        String requestedIndex = productCreationRequest.getIndexImage();
        if (requestedIndex != null && !requestedIndex.isEmpty()) {
            boolean exists = product.getMediaFiles().stream()
                    .anyMatch(media -> media.getSecureUrl().equals(requestedIndex));

            if (exists) {
                product.setIndexImage(requestedIndex);
            }
        }

        Long categoryId = productCreationRequest.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        product.setCategory(category);

        if (idsToDelete != null && !idsToDelete.isEmpty()) {
            List<Media> mediasToDelete = mediaRepository.findAllById(idsToDelete);
            for (Media media : mediasToDelete) {
                if (media.getPublicId() != null) {
                    cloudinaryService.deleteImage(media.getPublicId());
                }

                if (media.getSecureUrl().equals(product.getIndexImage())) {
                    product.setIndexImage("");
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

                    if (product.getIndexImage() == null || product.getIndexImage().isEmpty()) { // <--- SỬA
                        product.setIndexImage(res.getSecureUrl()); // <--- SỬA
                    }
                }
            }
            // Lưu ý: Vì CascadeType.ALL, chỉ cần save Product là Media tự lưu
            // Nhưng nếu muốn chắc ăn, có thể save mediaRepository.saveAll(...)
        }

        // logic dự phòng
        if ((product.getIndexImage() == null || product.getIndexImage().isEmpty()) && !product.getMediaFiles().isEmpty()) { // <--- SỬA
            String fallbackUrl = product.getMediaFiles().iterator().next().getSecureUrl();
            product.setIndexImage(fallbackUrl); // <--- SỬA
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductCreationResponse(savedProduct);
    }

    @Override
    @PreAuthorize("hasAuthority('PAGE_MANAGER')")
    public PageResponse<ProductCreationResponse> getProductsWithPagination(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        Page<Product> productPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductCreationResponse> dtos = productPage.getContent().stream()
                .map(productMapper::toProductCreationResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProductCreationResponse>builder()
                .content(dtos)
                .currentPage(page)
                .pageSize(productPage.getSize())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .build();
    }

    @Override
    public List<ProductCreationResponse> semanticSearch(String query) {
        return embeddingService.search(query).stream().map(i -> {
                        Product product = productRepository.findById(Long.valueOf(i.getId()))
                                .orElseThrow();
                        return productMapper.toProductCreationResponse(product);
                    }
                ).toList();
    }

    @Override
    public PageResponse<ProductCreationResponse> getProductsWithPaginationAndFilter(int page, int size, String keyword, Long categoryId) {
        // 1. Tạo Pageable
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        // 2. Gọi Repository (Hàm searchProducts vừa viết ở trên)
        Page<Product> productPage = productRepository.searchProducts(keyword, categoryId, pageable);

        // 3. Map kết quả sang DTO
        List<ProductCreationResponse> productResponses = productPage.getContent().stream()
                .map(productMapper::toProductCreationResponse)
                .toList();

        // 4. Trả về
        return PageResponse.<ProductCreationResponse>builder()
                .currentPage(page)
                .totalPages(productPage.getTotalPages())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .content(productResponses)
                .build();
    }


}
