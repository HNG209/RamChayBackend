package iuh.fit.se.dtos.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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
}
