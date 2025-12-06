package iuh.fit.se.dtos.response;

import iuh.fit.se.entities.Permission;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleFindResponse {
    Long roleId;
    String name;
    String description;
    Set<Permission> permissionIds;
}
