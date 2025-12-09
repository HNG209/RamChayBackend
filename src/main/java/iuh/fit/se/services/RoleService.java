package iuh.fit.se.services;


import iuh.fit.se.dtos.request.PermissionCreationRequest;
import iuh.fit.se.dtos.request.RoleCreationRequest;
import iuh.fit.se.dtos.request.RoleDeleteRequest;
import iuh.fit.se.dtos.response.RoleCreationResponse;
import iuh.fit.se.dtos.response.RoleDeleteResponse;
import iuh.fit.se.dtos.response.RoleFindResponse;
import iuh.fit.se.dtos.response.RolePaginationResponse;
import iuh.fit.se.entities.Role;

public interface RoleService {
    Role findByName(String name);

    RoleCreationResponse createRole(RoleCreationRequest dto);

    RoleCreationResponse updateRole(Long roleId, RoleCreationRequest dto);

    RoleFindResponse getRoleById(Long roleId);

    RoleDeleteResponse deleteRole(RoleDeleteRequest roleId);

    RolePaginationResponse getRoles(int page, int pageSize, String keyWord);


}
