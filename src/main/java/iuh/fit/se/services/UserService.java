package iuh.fit.se.services;

import iuh.fit.se.entities.User;

public interface UserService {
    User findByUsername(String username);
}
