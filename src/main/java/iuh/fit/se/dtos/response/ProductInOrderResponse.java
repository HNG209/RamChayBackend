package iuh.fit.se.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
/**
 * DTO hiển thị thông tin sản phẩm trong đơn hàng
 */
public class ProductInOrderResponse {
    Long id;
    String name;
    double price;
}

