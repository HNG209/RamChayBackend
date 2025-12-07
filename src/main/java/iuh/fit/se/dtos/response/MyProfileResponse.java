package iuh.fit.se.dtos.response;

import iuh.fit.se.entities.Address;
import iuh.fit.se.entities.Permission;
import iuh.fit.se.entities.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyProfileResponse {
    // Thông tin chung
    Long id;
    String username;
    String fullName;
    Set<String> roles;
    Set<String> permissions;

    // Thông tin riêng của Customer
    Set<String> phones;
    Set<Address> addresses;
}
