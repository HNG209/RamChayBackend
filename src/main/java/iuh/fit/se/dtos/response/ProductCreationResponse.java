package iuh.fit.se.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationResponse {
    Long id;
    String name;
    String description;
    double price;
    int stock;
    String unit;
    String indexImage;
    CategoryCreationResponse category;
    List<MediaUploadResponse> mediaList;
}
