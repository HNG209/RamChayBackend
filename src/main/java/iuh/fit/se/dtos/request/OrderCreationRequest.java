package iuh.fit.se.dtos.request;

import iuh.fit.se.entities.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationRequest {
    // Có thể null cho khách vãng lai
    Long customerId;

    @NotBlank(message = "Receiver name is required")
    String receiverName;

    @NotBlank(message = "Receiver phone is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone must be 10-11 digits")
    String receiverPhone;

    @NotBlank(message = "Shipping address is required")
    String shippingAddress;

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod;

    // Danh sách các item được chọn từ cart
    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    List<OrderItemCreationRequest> items;
}
