package iuh.fit.se.services;

import iuh.fit.se.dtos.request.PermissionCreationRequest;
import iuh.fit.se.dtos.response.PermissionCreationResponse;

public interface PermissionService  {
    PermissionCreationResponse createPermission(PermissionCreationRequest permissionCreationRequest);
}
