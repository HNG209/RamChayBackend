package iuh.fit.se.dtos.response;

import iuh.fit.se.entities.Customer;
import iuh.fit.se.entities.OrderDetail;
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

    String shippingAddress;

    String customerPhone;

    Customer customer;

    List<OrderDetail> orderDetails;
}
