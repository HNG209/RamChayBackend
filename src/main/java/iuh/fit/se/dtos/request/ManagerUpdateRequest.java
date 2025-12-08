package iuh.fit.se.dtos.request;

import iuh.fit.se.entities.Role;
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
public class ManagerUpdateRequest {

    @NotBlank(message = "MANAGER_USERNAME_INVALID")
    @Pattern(regexp = ".*\\S.*", message = "MANAGER_USERNAME_INVALID")
    @Size(min = 6, message = "MANAGER_USERNAME_SIZE_INVALID")
    String username;

    @Size(min = 6, message = "MANAGER_FULLNAME_SIZE_INVALID")
    @Pattern(regexp = ".*\\S.*", message = "MANAGER_FULLNAME_INVALID")
    @NotBlank(message = "MANAGER_FULLNAME_INVALID")
    String fullName;

    boolean active;

    @Size(min = 6, message = "PASSWORD_SIZE_INVALID")
    String password;

    @Size(min = 1, message = "ROLE_SIZE_INVALID")
    @NotNull(message = "ROLE_INVALID")
    Set<Role> roles;

}
