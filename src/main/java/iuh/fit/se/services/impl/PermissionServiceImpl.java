package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.PermissionCreationRequest;
import iuh.fit.se.dtos.response.PermissionCreationResponse;
import iuh.fit.se.dtos.response.PermissionPaginationResponse;
import iuh.fit.se.dtos.response.RolePaginationResponse;
import iuh.fit.se.entities.Permission;
import iuh.fit.se.mappers.PermissionMapper;
import iuh.fit.se.repositories.PermissionRepository;
import iuh.fit.se.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @PreAuthorize("hasAuthority('GETALL_PERMISSION')")
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @PreAuthorize("hasAuthority('CREATE_PERMISSION')")
    @Override
    @Transactional
    public PermissionCreationResponse createPermission(PermissionCreationRequest permissionCreationRequest) {
        Optional<Permission> permissionOptional = permissionRepository.findByName(permissionCreationRequest.getName());

//        if (permissionOptional.isPresent())
//            throw new AppException(ErrorCode.PERMISSION_EXISTED);

        Permission newPermission = new Permission();
        newPermission.setName(permissionCreationRequest.getName());

        Permission savedPermission = permissionRepository.save(newPermission);
        return permissionMapper.toPermissionCreationResponse(savedPermission);
    }
    @PreAuthorize("hasAuthority('PAGE_PERMISSION')")
    @Override
    public PermissionPaginationResponse getPermission(int page, int pageSize, String keyWord) {
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Permission> permissionPage;

        if (keyWord != null && !keyWord.trim().isEmpty()) {
            permissionPage = permissionRepository.findByNameContaining(keyWord.trim(), pageable);
        } else {
            permissionPage = permissionRepository.findAll(pageable);
        }

        return PermissionPaginationResponse.builder()
                .items(permissionPage.getContent()
                        .stream()
                        .map(permissionMapper::toPermissionPaginationItem)
                        .toList()
                )
                .pageNumber(permissionPage.getNumber())
                .pageSize(permissionPage.getSize())
                .totalPages(permissionPage.getTotalPages())
                .totalElements(permissionPage.getTotalElements())
                .last(permissionPage.isLast())
                .first(permissionPage.isFirst())
                .numberOfElements(permissionPage.getNumberOfElements())
                .build();
    }

}


