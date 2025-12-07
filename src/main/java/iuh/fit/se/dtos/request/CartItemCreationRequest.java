package iuh.fit.se.dtos.request;


import iuh.fit.se.entities.Cart;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemCreationRequest {
    Long productId;

    @Min(value = 1, message = "INVALID_QUANTITY")
    int quantity;
//    double unitPrice;
//    double subtotal;
//    Long cartId;
}
