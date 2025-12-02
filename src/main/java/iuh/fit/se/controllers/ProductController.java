package iuh.fit.se.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.dtos.request.MediaUploadRequest;
import iuh.fit.se.dtos.request.ProductCreationRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.ProductCreationResponse;
import iuh.fit.se.services.ProductService;
import iuh.fit.se.services.cloud.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

//        request.setMediaUploadRequests(mediaSet);
//        request.setImageUrl(null);

        return ApiResponse.<ProductCreationResponse>builder()
                .result(productService.createProduct(request, images))
                .build();
    }
}
