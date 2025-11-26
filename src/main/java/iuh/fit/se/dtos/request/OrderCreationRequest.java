package iuh.fit.se.dtos.request;

import iuh.fit.se.entities.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationRequest {
    String shippingAddress;
    String customerPhone;
    PaymentMethod paymentMethod;
}
