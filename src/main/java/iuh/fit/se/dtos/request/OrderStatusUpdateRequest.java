package iuh.fit.se.dtos.request;

import iuh.fit.se.entities.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusUpdateRequest {
    @NotNull(message = "Order status is required")
    OrderStatus orderStatus;
}

