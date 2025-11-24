package iuh.fit.se.dtos.request;


import iuh.fit.se.entities.Cart;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemCreationRequest {
    int quantity;
    double unitPrice;
    double subtotal;
    Long productId;
    Long cartId;
}
