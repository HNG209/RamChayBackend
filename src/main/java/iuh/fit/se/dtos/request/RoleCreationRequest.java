package iuh.fit.se.dtos.request;

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
            regexp = "^ROLE_[A-Z]+$",
            message = "Tên role phải theo dạng ROLE_ và phía sau là danh từ in hoa"
    )
    String name;

    String description;

    @Size(min = 1, message = "Danh sách quyền phải có ít nhất 1 quyền")
    @NotNull(message = "Danh sách quyền không được null")
    Set<@NotNull Long> permissionIds;
}
