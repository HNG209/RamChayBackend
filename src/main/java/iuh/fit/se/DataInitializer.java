package iuh.fit.se;

import iuh.fit.se.entities.Role;
import iuh.fit.se.entities.User;
import iuh.fit.se.repositories.RoleRepository;
import iuh.fit.se.repositories.UserRepository;
import iuh.fit.se.services.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        boolean hasAdmin = userRepository.existsByRoles_Name("ROLE_ADMIN");

        if(!hasAdmin) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new IllegalArgumentException("Admin role not specified"));

            User admin = User.builder()
                    .username("admin")
                    .password(BCrypt.hashpw("admin123", BCrypt.gensalt()))
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(admin);
            System.out.println("Admin initialized");
        }
    }
}
