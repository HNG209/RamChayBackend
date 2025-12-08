package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.ManagerCreationRequest;
import iuh.fit.se.dtos.request.ManagerDeleteRequest;
import iuh.fit.se.dtos.request.ManagerFindRequest;
import iuh.fit.se.dtos.request.ManagerUpdateRequest;
import iuh.fit.se.dtos.response.*;
import iuh.fit.se.entities.Role;
import iuh.fit.se.entities.User;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.ManagerMapper;
import iuh.fit.se.repositories.RoleRepository;
import iuh.fit.se.repositories.UserRepository;
import iuh.fit.se.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        System.out.println("ROLE ID = " + managerCreationRequest.getRoles());
        String hashed = BCrypt.hashpw(managerCreationRequest.getPassword(), BCrypt.gensalt());
        List<Long> roleIds = managerCreationRequest.getRoles().stream().toList();
        Set<Role> roles = roleIds.stream()
                .map(id -> roleRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + id)))
                .collect(Collectors.toSet());

        User user = User.builder()
                .username(managerCreationRequest.getUsername())
                .password(hashed)
                .fullName(managerCreationRequest.getFullName())
                .active(managerCreationRequest.isActive())
                .roles(roles)
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

        user.setActive(request.isActive());

        if (request.getUsername() != null)
            user.setUsername(request.getUsername());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String hashed = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
            user.setPassword(hashed);
        }

        if(request.getRoles() != null && !request.getRoles().isEmpty()){
            user.setRoles(request.getRoles());
        }

        userRepository.save(user);

        return managerMapper.toManagerUpdateResponse(user);
    }

    @PreAuthorize("hasAuthority('SEARCH_MANAGER')")
    @Override
    public Page<User> searchUsers(String keyword, int page) {
        Pageable pageable = PageRequest.of(page, 6); // 5 phần tử / trang

        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(pageable); // Không có keyword → lấy tất cả
        }

        return userRepository.searchByKeyword(keyword, pageable);
    }

    @PreAuthorize("hasAuthority('FIND_MANAGER')")
    @Override
    public ManagerFindResponse findManager(ManagerFindRequest request) {
        User manager = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return ManagerFindResponse.builder()
                .id(manager.getId())
                .username(manager.getUsername())
                .fullName(manager.getFullName())
                .active(manager.isActive())
                .roles(manager.getRoles())
                .build();
    }

    @PreAuthorize("hasAuthority('PAGE_MANAGER')")
    @Override
    public ManagerPaginationResponse getManagers(int page, int pageSize, String keyWord) {
        // Tạo đối tượng Pageable (sử dụng 0-based index)
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<User> userPage;

        if (keyWord != null && !keyWord.trim().isEmpty()) {
            // Trường hợp 1: Có từ khóa tìm kiếm
            String searchPattern = "%" + keyWord.trim() + "%";

            // Cần phương thức tìm kiếm trong Repository
            userPage = userRepository.findByFullNameContaining(keyWord.trim(), pageable);
        } else {
            // Trường hợp 2: Không có từ khóa (phân trang thuần túy)
            userPage = userRepository.findAll(pageable);
            System.out.println(userPage.getSize());
        }

        // Chuyển đổi Page<User> thành ManagerPaginationResponse
        return ManagerPaginationResponse.builder()
                .items(userPage.getContent()
                        .stream()
                        .map(managerMapper::toManagerFindResponse)
                        .toList()
                )
                .pageNumber(userPage.getNumber())          // Số trang hiện tại (0-based)
                .pageSize(userPage.getSize())              // Kích thước trang (thường là pageSize)
                .totalPages(userPage.getTotalPages())      // Tổng số trang
                .totalElements(userPage.getTotalElements())// Tổng số phần tử
                .last(userPage.isLast())                   // Là trang cuối cùng
                .first(userPage.isFirst())                 // Là trang đầu tiên
                .numberOfElements(userPage.getNumberOfElements()) // Số phần tử trong trang hiện tại
                .build();
    }
}


