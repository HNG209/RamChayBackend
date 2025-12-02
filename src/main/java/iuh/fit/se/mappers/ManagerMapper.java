package iuh.fit.se.mappers;

import iuh.fit.se.dtos.request.ManagerCreationRequest;
import iuh.fit.se.dtos.response.ManagerCreationResponse;
import iuh.fit.se.entities.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ManagerMapper {
    ManagerCreationResponse toManagerCreationResponse(User user);
}
