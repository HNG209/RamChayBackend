package iuh.fit.se.repositories;

import iuh.fit.se.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    // Các query khác không fetch roles và permissions(LAZY)
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByUsername(String username);
}
