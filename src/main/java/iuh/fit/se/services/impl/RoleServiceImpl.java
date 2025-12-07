package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.RoleCreationRequest;
import iuh.fit.se.dtos.request.RoleDeleteRequest;
import iuh.fit.se.dtos.request.RolePaginationRequest;
import iuh.fit.se.dtos.response.*;
import iuh.fit.se.entities.Permission;
import iuh.fit.se.entities.Role;
import iuh.fit.se.entities.User;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.RoleMapper;
import iuh.fit.se.repositories.PermissionRepository;
import iuh.fit.se.repositories.RoleRepository;
import iuh.fit.se.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        if (dto.getPermissionIds() != null)
            role.setPermissions(permissions);

        roleRepository.save(role);

        return roleMapper.toRoleCreationResponse(role);
    }

    @PreAuthorize("hasAuthority('FINDONE_ROLE')")
    @Override
    public RoleFindResponse getRoleById(Long roleId) {
//        tim role
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        return RoleFindResponse.builder()
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

    @PreAuthorize("hasAuthority('PAGE_ROLE')")
    @Override
    public RolePaginationResponse getRoles(int page, int pageSize, String keyWord) {
        // Tạo đối tượng Pageable (sử dụng 0-based index)
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Role> rolePage;

        if (keyWord != null && !keyWord.trim().isEmpty()) {
            // Trường hợp 1: Có từ khóa tìm kiếm
            String searchPattern = "%" + keyWord.trim() + "%";

            // Cần phương thức tìm kiếm trong Repository
            rolePage = roleRepository.findByNameContaining(keyWord.trim(), pageable);
        } else {
            // Trường hợp 2: Không có từ khóa (phân trang thuần túy)
            rolePage = roleRepository.findAll(pageable);
            System.out.println(rolePage.getSize());
        }

        // Chuyển đổi Page<Role> thành ManagerPaginationResponse
        return RolePaginationResponse.builder()
                .items(rolePage.getContent()
                        .stream()
                        .map(roleMapper::toRoleFindResponse)
                        .toList()
                )
                .pageNumber(rolePage.getNumber())          // Số trang hiện tại (0-based)
                .pageSize(rolePage.getSize())              // Kích thước trang (thường là pageSize)
                .totalPages(rolePage.getTotalPages())      // Tổng số trang
                .totalElements(rolePage.getTotalElements())// Tổng số phần tử
                .last(rolePage.isLast())                   // Là trang cuối cùng
                .first(rolePage.isFirst())                 // Là trang đầu tiên
                .numberOfElements(rolePage.getNumberOfElements()) // Số phần tử trong trang hiện tại
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
