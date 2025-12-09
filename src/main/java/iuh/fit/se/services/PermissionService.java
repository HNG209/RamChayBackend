package iuh.fit.se.services;

import iuh.fit.se.dtos.request.PermissionCreationRequest;
import iuh.fit.se.dtos.request.PermissionDeleteRequest;
import iuh.fit.se.dtos.request.RoleDeleteRequest;
import iuh.fit.se.dtos.response.*;

public interface PermissionService  {
    PermissionCreationResponse createPermission(PermissionCreationRequest permissionCreationRequest);
    PermissionPaginationResponse getPermission(int page, int pageSize, String keyWord);
    PermissionDeleteResponse deletePermission(PermissionDeleteRequest id);
}
