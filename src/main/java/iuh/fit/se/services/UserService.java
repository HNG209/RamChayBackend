package iuh.fit.se.services;

import iuh.fit.se.dtos.request.ManagerCreationRequest;
import iuh.fit.se.dtos.request.ManagerDeleteRequest;
import iuh.fit.se.dtos.request.ManagerUpdateRequest;
import iuh.fit.se.dtos.response.ManagerCreationResponse;
import iuh.fit.se.dtos.response.ManagerDeleteResponse;
import iuh.fit.se.dtos.response.ManagerUpdateResponse;
import iuh.fit.se.entities.User;

public interface UserService {
    User findByUsername(String username);

    ManagerCreationResponse createManager(ManagerCreationRequest managerCreationRequest);

    ManagerDeleteResponse deleteManager(ManagerDeleteRequest managerDeleteRequest);

    ManagerUpdateResponse updateManager(Long id, ManagerUpdateRequest managerUpdateRequest);


}
