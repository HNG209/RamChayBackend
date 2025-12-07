package iuh.fit.se.dtos.request;

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
    String name;
    String description;
    double price;
    int stock;
    String unit;
    String indexImage;
    CategoryCreationRequest category;
    Set<MediaUploadRequest> mediaUploadRequests;
    List<Long> imageIdsToDelete;

}
