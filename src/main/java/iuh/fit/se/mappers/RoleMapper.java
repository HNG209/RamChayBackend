package iuh.fit.se.mappers;

import iuh.fit.se.dtos.response.RoleCreationResponse;
import iuh.fit.se.dtos.response.RoleDeleteResponse;
import iuh.fit.se.dtos.response.RoleFindResponse;
import iuh.fit.se.entities.Role;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RoleMapper {

    RoleCreationResponse toRoleCreationResponse(Role role);

    RoleFindResponse toRoleFindResponse(Role role);

    RoleDeleteResponse toRoleDeleteResponse(Role role);
    default Long map(Role role) {
        return role.getId();
    }

    default Set<Long> map(Set<Role> roles) {
        return roles.stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
    }
}
