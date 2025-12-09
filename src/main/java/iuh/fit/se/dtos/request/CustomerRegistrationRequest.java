package iuh.fit.se.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "USERNAME_REQUIRED")
    String username;

    @Size(min = 6, message = "PASSWORD_SIZE_INVALID")
    String password; // plain password

    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    String email; // Email bắt buộc, độc lập với username

    String fullName;

    @Pattern(regexp = "^\\d{10}$", message = "PHONE_INVALID")
    String phone;
}
