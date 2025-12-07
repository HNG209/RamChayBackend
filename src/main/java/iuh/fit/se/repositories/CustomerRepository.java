package iuh.fit.se.repositories;

import iuh.fit.se.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {// repository của sub class
    Optional<Customer> findByUsername(String username);
    Optional<Customer> getCustomerById(Long id);

}
