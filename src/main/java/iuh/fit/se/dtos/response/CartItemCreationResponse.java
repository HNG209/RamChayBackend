package iuh.fit.se.dtos.response;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemCreationResponse {
    Long id;
    int quantity;
    double unitPrice;
    double subtotal;
}
