package iuh.fit.se.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {
    @NotBlank(message = "PRODUCT_NAME_INVALID")
    @Size(min = 3, message = "PRODUCT_NAME_INVALID")
    String name;

    String description;

    @NotNull(message = "PRODUCT_PRICE_MISSING")
    @Min(value = 0, message = "PRODUCT_PRICE_INVALID")
    Double price;

    @NotNull(message = "PRODUCT_STOCK_MISSING")
    @Min(value = 0, message = "PRODUCT_STOCK_INVALID")
    Integer stock;

    @NotBlank(message = "PRODUCT_UNIT_INVALID")
    String unit;

    @NotNull(message = "PRODUCT_CATEGORY_INVALID")
    Long categoryId;

    String indexImage;
    Set<MediaUploadRequest> mediaUploadRequests;
    List<Long> imageIdsToDelete;

}
