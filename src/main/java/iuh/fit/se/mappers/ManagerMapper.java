package iuh.fit.se.mappers;

import iuh.fit.se.dtos.response.ManagerCreationResponse;
import iuh.fit.se.dtos.response.ManagerDeleteResponse;
import iuh.fit.se.dtos.response.ManagerFindResponse;
import iuh.fit.se.dtos.response.ManagerUpdateResponse;
import iuh.fit.se.entities.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ManagerMapper {
    ManagerCreationResponse toManagerCreationResponse(User user);

    ManagerDeleteResponse toManagerDeleteResponse(User user);

    ManagerUpdateResponse toManagerUpdateResponse(User user);
    ManagerFindResponse toManagerFindResponse(User user);
}
