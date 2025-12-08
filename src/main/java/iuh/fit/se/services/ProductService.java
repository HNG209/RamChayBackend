package iuh.fit.se.services;

import iuh.fit.se.dtos.request.ProductCreationRequest;
import iuh.fit.se.dtos.response.ProductCreationResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductCreationResponse createProduct(ProductCreationRequest productCreationRequest, List<MultipartFile> images) throws IOException;

    List<ProductCreationResponse> getAllProducts();

    ProductCreationResponse getProductById(Long id);

    void deleteProduct(Long productId) throws IOException;

    ProductCreationResponse updateProduct(Long productId, ProductCreationRequest productCreationRequest, List<MultipartFile> images) throws IOException;
}
