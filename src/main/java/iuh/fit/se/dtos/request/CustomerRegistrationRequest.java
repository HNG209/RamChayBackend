package iuh.fit.se.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRegistrationRequest {
    String username;

    @Size(min = 6, message = "PASSWORD_SIZE_INVALID")
    String password; // plain password

    String fullName;

    @Pattern(regexp = "^\\d{10}$", message = "PHONE_INVALID")
    String phone;
}
