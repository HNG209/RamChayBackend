package iuh.fit.se.services.impl;

import iuh.fit.se.entities.Permission;
import iuh.fit.se.repositories.PermissionRepository;
import iuh.fit.se.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final   PermissionRepository permissionRepository;

    @PreAuthorize("hasAuthority('GETALL_PERMISSION')")
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

}
