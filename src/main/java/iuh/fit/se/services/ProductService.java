package iuh.fit.se.services;

import iuh.fit.se.dtos.request.ProductCreationRequest;
import iuh.fit.se.dtos.response.PageResponse;
import iuh.fit.se.dtos.response.ProductCreationResponse;
import iuh.fit.se.entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductCreationResponse createProduct(ProductCreationRequest productCreationRequest, List<MultipartFile> images) throws IOException;

    List<ProductCreationResponse> getAllProducts();

    List<ProductCreationResponse> findProductsByCategoryId(Long categoryId);

    ProductCreationResponse getProductById(Long id);

    void deleteProduct(Long productId) throws IOException;

    ProductCreationResponse updateProduct(Long productId, ProductCreationRequest productCreationRequest, List<MultipartFile> images) throws IOException;

    PageResponse<ProductCreationResponse> getProductsWithPagination(int page, int size, String keyword);

    PageResponse<ProductCreationResponse> getProductsWithPaginationAndFilter(int page, int size, String keyword, Long categoryId);
}
