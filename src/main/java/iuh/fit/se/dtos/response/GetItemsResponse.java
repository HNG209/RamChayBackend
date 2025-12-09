package iuh.fit.se.dtos.response;

import iuh.fit.se.entities.Media;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetItemsResponse {
    Long id;
    int quantity;
//    double subtotal;

    Long productId;
    String productName;
    double unitPrice;
    String indexImage;
}
