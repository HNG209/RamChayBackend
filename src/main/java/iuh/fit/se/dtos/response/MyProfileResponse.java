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
//    String fullName; sẽ đổi thành displayName
    Set<String> roles;
    Set<String> permissions;

    // Thông tin riêng của Customer
    String fullName;
    Set<String> phones;
    Set<Address> addresses;
}
