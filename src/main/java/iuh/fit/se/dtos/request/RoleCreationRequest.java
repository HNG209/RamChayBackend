package iuh.fit.se.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleCreationRequest {

    @Pattern(
            regexp = "^ROLE_[A-Z]+(?:_[A-Z]+)*$",
            message = "ROLE_NAME_INVALID"
    )
    @Pattern(regexp = ".*\\S.*", message = "ROLE_NAME_SIZE_INVALID")
    @NotBlank(message ="ROLE_NAME_SIZE_INVALID")
    String name;

    @Size(min = 10, message = "ROLE_DESCRIPTION_SIZE_INVALID")
    @NotBlank(message ="ROLE_DESCRIPTION_INVALID")
    @Pattern(regexp = ".*\\S.*", message = "ROLE_DESCRIPTION_INVALID")
    String description;

    @Size(min = 1, message = "PERMISSION_SIZE_INVALID")
    @NotNull(message = "PERMISSION_INVALID")
    Set<@NotNull Long> permissionIds;
}
