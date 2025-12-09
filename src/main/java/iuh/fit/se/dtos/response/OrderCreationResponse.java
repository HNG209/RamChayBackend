package iuh.fit.se.dtos.response;

import iuh.fit.se.entities.enums.OrderStatus;
import iuh.fit.se.entities.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationResponse {
    Long id;

    LocalDateTime orderDate;

    double total;

    OrderStatus orderStatus;

    PaymentMethod paymentMethod;

    String receiverName;

    String receiverPhone;

    String shippingAddress;

    // Thông tin khách hàng (null nếu là khách vãng lai)
    CustomerInOrderResponse customer;

    // Danh sách chi tiết đơn hàng
    List<OrderDetailResponse> orderDetails;
}
