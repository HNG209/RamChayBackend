package iuh.fit.se.dtos.request;

import iuh.fit.se.entities.Role;
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
public class ManagerUpdateRequest {

    @Size(min = 6, message = "Tên người dùng phải có tối thiểu là 6 kí tự")
    String username;

    @Size(min = 6, message = "Tên người dùng phải có tối thiểu là 6 kí tự")
    String fullName;

    boolean active;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$",
            message = "Mật khẩu phải ít nhất 6 ký tự, gồm ít nhất 1 chữ cái và 1 số"
    )
    String password;

    @Size(min = 1, message = "Vai trò phải có ít nhất 1 quyền")
    @NotNull(message = "Danh sách vai trò không được null")
    Set<Role> roles;



}
