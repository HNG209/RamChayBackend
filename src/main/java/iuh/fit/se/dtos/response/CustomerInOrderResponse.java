package iuh.fit.se.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
/**
 * DTO hiển thị thông tin khách hàng trong đơn hàng (không bao gồm thông tin nhạy cảm)
 */
public class CustomerInOrderResponse {
    Long id;
    String username;
    String fullName;
}

