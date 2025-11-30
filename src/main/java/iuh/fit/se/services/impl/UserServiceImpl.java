package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.ManagerCreationRequest;
import iuh.fit.se.dtos.request.ManagerDeleteRequest;
import iuh.fit.se.dtos.request.ManagerUpdateRequest;
import iuh.fit.se.dtos.response.ManagerCreationResponse;
import iuh.fit.se.dtos.response.ManagerDeleteResponse;
import iuh.fit.se.dtos.response.ManagerUpdateResponse;
import iuh.fit.se.entities.Role;
import iuh.fit.se.entities.User;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.ManagerMapper;
import iuh.fit.se.repositories.RoleRepository;
import iuh.fit.se.repositories.UserRepository;
import iuh.fit.se.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ManagerMapper managerMapper;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }

    @Override
    @PreAuthorize("hasAuthority('ADD_MANAGER')")
    @Transactional
    public ManagerCreationResponse createManager(ManagerCreationRequest managerCreationRequest) {

        Role role = roleRepository.findById(managerCreationRequest.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        String hashed = BCrypt.hashpw(managerCreationRequest.getPassword(), BCrypt.gensalt());
        User user = User.builder()
                .username(managerCreationRequest.getUsername())
                .password(hashed)
                .fullName(managerCreationRequest.getFullName())
                .roles(Set.of(role))
                .build();

        User userSaved = userRepository.save(user);

        return managerMapper.toManagerCreationResponse(userSaved);
    }

    @Override
    @PreAuthorize("hasAuthority('DELETE_MANAGER')")
    public ManagerDeleteResponse deleteManager(ManagerDeleteRequest managerDeleteRequest) {

        User user = userRepository.findById(managerDeleteRequest.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setActive(false);
        userRepository.save(user);
        return managerMapper.toManagerDeleteResponse(user);
    }

    @Override
    @PreAuthorize("hasAuthority('UPDATE_MANAGER')")
    public ManagerUpdateResponse updateManager(Long id, ManagerUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getFullName() != null)
            user.setFullName(request.getFullName());

        if (request.isActive())
            user.setActive(request.isActive());

        if (request.getUsername() != null)
            user.setUsername(request.getUsername());

        userRepository.save(user);

        return managerMapper.toManagerUpdateResponse(user);
    }

}
