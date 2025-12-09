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
    // Thông tin chung
    Long id;
    String username;
    String email; // Email của user (có thể giống username hoặc riêng biệt)
    String fullName;
    Set<String> roles;
    Set<String> permissions;

    // Thông tin riêng của Customer
    Set<String> phones;
    Set<Address> addresses;
}
