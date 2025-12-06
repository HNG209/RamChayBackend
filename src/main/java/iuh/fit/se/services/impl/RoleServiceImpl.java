package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.RoleCreationRequest;
import iuh.fit.se.dtos.request.RoleDeleteRequest;
import iuh.fit.se.dtos.response.RoleCreationResponse;
import iuh.fit.se.dtos.response.RoleDeleteResponse;
import iuh.fit.se.dtos.response.RoleFindResponse;
import iuh.fit.se.entities.Permission;
import iuh.fit.se.entities.Role;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.RoleMapper;
import iuh.fit.se.repositories.PermissionRepository;
import iuh.fit.se.repositories.RoleRepository;
import iuh.fit.se.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    }

    @Override
    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    public RoleCreationResponse createRole(RoleCreationRequest dto) {
        roleRepository.findByName(dto.getName()).ifPresent(r -> {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        });

        // set permission
        Set<Permission> permissions = dto.getPermissionIds().stream()
                .map(id -> permissionRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND))).collect(Collectors.toSet());

        Role role = Role.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .permissions(permissions)
                .build();

        Role roleSaved = roleRepository.save(role);
        return roleMapper.toRoleCreationResponse(roleSaved);

    }

    @Override
    @PreAuthorize("hasAuthority('UPDATE_ROLE')")
    public RoleCreationResponse updateRole(Long roleId, RoleCreationRequest dto) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        Set<Permission> permissions = dto.getPermissionIds().stream()
                .map(id -> permissionRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND))).collect(Collectors.toSet());

        if (dto.getName() != null)
            role.setName(dto.getName());

        if (dto.getDescription() != null)
            role.setDescription(dto.getDescription());

        if(dto.getPermissionIds() != null)
            role.setPermissions(permissions);

        roleRepository.save(role);

        return roleMapper.toRoleCreationResponse(role);
    }
    @PreAuthorize("hasAuthority('FINDONE_ROLE')")
    @Override
    public RoleFindResponse getRoleById(Long roleId) {
//        tim role
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()->  new AppException(ErrorCode.ROLE_NOT_FOUND));

        return  RoleFindResponse.builder()
                .roleId(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissionIds(role.getPermissions())
                .build();
    }

    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    @Override
    public RoleDeleteResponse deleteRole(RoleDeleteRequest roleId) {
        // 1. Kiểm tra Role có tồn tại không
        Role role = roleRepository.findById(roleId.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        // 2. Xóa Role
        roleRepository.deleteById(roleId.getId());

        // 3. Trả về response
        return RoleDeleteResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    @PreAuthorize("hasAuthority('GET_ROLE')")
    public List<Role> getRoles() {
        return roleRepository.findAllExceptCustomer();
    }

    @PreAuthorize("hasAuthority('GETALL_ROLE')")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

}
