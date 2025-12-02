package iuh.fit.se.services;

import iuh.fit.se.dtos.request.ManagerCreationRequest;
import iuh.fit.se.dtos.response.ManagerCreationResponse;
import iuh.fit.se.entities.User;

public interface UserService {
    User findByUsername(String username);
    ManagerCreationResponse  createManager(ManagerCreationRequest managerCreationRequest);

}
