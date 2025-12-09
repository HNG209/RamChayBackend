package iuh.fit.se.mappers;

import iuh.fit.se.dtos.request.PermissionCreationRequest;
import iuh.fit.se.dtos.response.PermissionCreationResponse;
import iuh.fit.se.dtos.response.PermissionPaginationItemResponse;
import iuh.fit.se.dtos.response.PermissionPaginationResponse;
import iuh.fit.se.entities.Permission;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))

public interface PermissionMapper {
    PermissionCreationResponse  toPermissionCreationResponse(Permission permission);
    PermissionPaginationItemResponse toPermissionPaginationItem(Permission permission);
}
