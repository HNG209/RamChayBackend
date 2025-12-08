package iuh.fit.se.dtos.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionCreationRequest {
    @NotBlank(message = "PERMISSION_NAME_INVALID")
    @Pattern(regexp = ".*\\S.*", message = "PERMISSION_NAME_INVALID")
    @Pattern(
            regexp = "^[A-Z]+(?:_[A-Z]+)+$",
            message = "PERMISSION_NAME_SIZE_INVALID"
    )
    String name;
}
