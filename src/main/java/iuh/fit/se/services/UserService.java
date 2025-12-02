package iuh.fit.se.services;

import iuh.fit.se.dtos.request.ManagerCreationRequest;
import iuh.fit.se.dtos.request.ManagerDeleteRequest;
import iuh.fit.se.dtos.request.ManagerFindRequest;
import iuh.fit.se.dtos.request.ManagerUpdateRequest;
import iuh.fit.se.dtos.response.*;
import iuh.fit.se.entities.User;
import org.springframework.data.domain.Page;

public interface UserService {
    User findByUsername(String username);

    ManagerCreationResponse createManager(ManagerCreationRequest managerCreationRequest);

    ManagerDeleteResponse deleteManager(ManagerDeleteRequest managerDeleteRequest);

    ManagerUpdateResponse updateManager(Long id, ManagerUpdateRequest managerUpdateRequest);

    Page<User> searchUsers(String keyword, int page);

    ManagerFindResponse findManager(ManagerFindRequest managerFindRequest);

    ManagerPaginationResponse getManagers(int page, int pageSize, String keyWord);
}
