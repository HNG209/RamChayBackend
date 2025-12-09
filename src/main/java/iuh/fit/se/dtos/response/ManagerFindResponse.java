package iuh.fit.se.dtos.response;


import iuh.fit.se.entities.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManagerFindResponse {
    Long id;
    String username;
    String fullName;
    boolean active;
    LocalDateTime createdAt;
    Set<Role> roles;
}
