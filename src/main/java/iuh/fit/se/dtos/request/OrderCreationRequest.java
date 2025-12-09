package iuh.fit.se.dtos.request;

import iuh.fit.se.entities.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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

    // Địa chỉ giao hàng: có thể là addressId (chọn từ saved) hoặc shippingAddress (nhập mới)
    Long addressId; // ID của địa chỉ đã lưu (optional)

    String shippingAddress; // Địa chỉ dạng string (optional, dùng khi không chọn addressId)

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod;

    // Email để gửi xác nhận đơn hàng (bắt buộc cho khách vãng lai, tùy chọn cho khách đăng nhập)
    @Email(message = "Invalid email format")
    String email;

    // Danh sách các item được chọn từ cart
    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    List<OrderItemCreationRequest> items;
}
