package iuh.fit.se.dtos.request;

import iuh.fit.se.entities.Address;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerUpdateRequest {
    String fullName;
    Set<String> phones;
    String email;
    Set<Address> addresses;
}
