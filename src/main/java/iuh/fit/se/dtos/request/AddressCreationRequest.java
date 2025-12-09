package iuh.fit.se.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressCreationRequest {
    @NotBlank(message = "City is required")
    String city;

    @NotBlank(message = "Ward is required")
    String ward;

    String street; // Tùy chọn

    @NotBlank(message = "Personal address is required")
    String personalAddress; // Số nhà, ngõ, etc.
}

