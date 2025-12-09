package iuh.fit.se.mappers;

import iuh.fit.se.dtos.response.ManagerCreationResponse;
import iuh.fit.se.dtos.response.ManagerDeleteResponse;
import iuh.fit.se.dtos.response.ManagerFindResponse;
import iuh.fit.se.dtos.response.ManagerUpdateResponse;
import iuh.fit.se.entities.Role;
import iuh.fit.se.entities.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ManagerMapper {
    ManagerCreationResponse toManagerCreationResponse(User user);

    ManagerDeleteResponse toManagerDeleteResponse(User user);

    ManagerUpdateResponse toManagerUpdateResponse(User user);

    ManagerFindResponse toManagerFindResponse(User user);

    default Long map(Role role) {
        return role.getId();
    }

    default Set<Long> map(Set<Role> roles) {
        return roles.stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
    }
}
