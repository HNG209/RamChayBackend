package iuh.fit.se.dtos.response;

import iuh.fit.se.entities.Address;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyProfileResponse {
    // Thông tin chung (User)
    Long id;
    String username;
    String fullName;
    Set<String> roles;
    Set<String> permissions;

    // Thông tin riêng của Customer (email chỉ có ở Customer)
    String email; // Email chỉ có ở Customer, Admin không có email
    Set<String> phones;
    Set<Address> addresses;
}
