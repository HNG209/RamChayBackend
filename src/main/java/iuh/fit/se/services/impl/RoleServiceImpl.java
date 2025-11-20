package iuh.fit.se.services.impl;

import iuh.fit.se.entities.Role;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.repositories.RoleRepository;
import iuh.fit.se.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    }
}
