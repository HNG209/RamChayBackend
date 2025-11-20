package iuh.fit.se.services;


import iuh.fit.se.entities.Role;

public interface RoleService {
    Role findByName(String name);
}
