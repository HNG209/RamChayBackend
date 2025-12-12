package iuh.fit.se.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.dtos.request.MediaUploadRequest;
import iuh.fit.se.dtos.request.ProductCreationRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.PageResponse;
import iuh.fit.se.dtos.response.ProductCreationResponse;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.services.ProductService;
import iuh.fit.se.services.cloud.CloudinaryService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    private void validateRequest(ProductCreationRequest request) {
        Set<ConstraintViolation<ProductCreationRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            // Lấy lỗi đầu tiên tìm thấy
            String message = violations.iterator().next().getMessage();
            try {
                // Chuyển message (ví dụ: "PRODUCT_NAME_INVALID") thành ErrorCode
                throw new AppException(ErrorCode.valueOf(message));
            } catch (IllegalArgumentException e) {
                // Fallback nếu message không khớp Enum
                throw new RuntimeException("Validation error: " + message);
            }
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProductCreationResponse> addProduct(
            @Parameter(schema = @Schema(implementation = ProductCreationRequest.class))
            @RequestPart("product") String productString,
            @RequestPart("image") List<MultipartFile> images) throws IOException {

        ProductCreationRequest request = null;
        try {
            request = objectMapper.readValue(productString, ProductCreationRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Dữ liệu JSON sản phẩm không hợp lệ!");
        }

        validateRequest(request);

        return ApiResponse.<ProductCreationResponse>builder()
                .result(productService.createProduct(request, images))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProductCreationResponse>> getAllProducts(
            @RequestParam(required = false) Long categoryId // Thêm tham số này (không bắt buộc)
    ) {
        List<ProductCreationResponse> result;

        if (categoryId != null && categoryId > 0) {
            // Nếu có categoryId -> Gọi hàm lọc bạn vừa viết
            result = productService.findProductsByCategoryId(categoryId);
        } else {
            // Nếu không có -> Lấy tất cả như cũ
            result = productService.getAllProducts();
        }

        return ApiResponse.<List<ProductCreationResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductCreationResponse> getProductById(@PathVariable Long id) {
        return ApiResponse.<ProductCreationResponse>builder()
                .result(productService.getProductById(id))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id) throws IOException {
        productService.deleteProduct(id);
        return ApiResponse.<String>builder()
                .result("Xóa sản phẩm thành công")
                .build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProductCreationResponse> updateProduct(
            @PathVariable Long id,
            @Parameter(schema = @Schema(implementation = ProductCreationRequest.class))
            @RequestPart("product") String productString,
            @RequestPart(value = "image", required = false) List<MultipartFile> images) throws IOException {

        ProductCreationRequest request = null;
        try {
            request = objectMapper.readValue(productString, ProductCreationRequest.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu JSON sản phẩm không hợp lệ!");
        }

        validateRequest(request);

        return ApiResponse.<ProductCreationResponse>builder()
                .result(productService.updateProduct(id, request, images))
                .build();
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<ProductCreationResponse>> getProductsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId
    ) {
        return ApiResponse.<PageResponse<ProductCreationResponse>>builder()
                .result(productService.getProductsWithPaginationAndFilter(page, size, keyword, categoryId))
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<ProductCreationResponse>> semanticSearch(@RequestParam String query) {
        return ApiResponse.<List<ProductCreationResponse>>builder()
                .result(productService.semanticSearch(query))
                .build();
    }
}
