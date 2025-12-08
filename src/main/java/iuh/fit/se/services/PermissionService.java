package iuh.fit.se.services;

import iuh.fit.se.dtos.request.PermissionCreationRequest;
import iuh.fit.se.dtos.response.PermissionCreationResponse;
import iuh.fit.se.dtos.response.PermissionPaginationResponse;
import iuh.fit.se.dtos.response.RolePaginationResponse;

public interface PermissionService  {
    PermissionCreationResponse createPermission(PermissionCreationRequest permissionCreationRequest);
    PermissionPaginationResponse getPermission(int page, int pageSize, String keyWord);
}
