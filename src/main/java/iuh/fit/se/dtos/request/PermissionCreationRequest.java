package iuh.fit.se.dtos.request;


import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionCreationRequest {
    @Pattern(
            regexp = "^[A-Z]+(_[A-Z]+)*$",
            message = "Tên vai trò chỉ được gồm chữ in hoa và dấu gạch dưới, ví dụ: ADD_VIEW"
    )
    String name;
}
