package iuh.fit.se.repositories;

import iuh.fit.se.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.orderDate DESC")
    List<Order> findAllByCustomerId(@Param("customerId") Long customerId);

    //Optional hạn chế vấn đề NullPointerException, bên service dùng isPresent() hoặc orElseThrow() để kiểm tra.
    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.customer.id = :customerId")
    Optional<Order> findByIdAndCustomerId(@Param("orderId") Long orderId, @Param("customerId") Long customerId);

    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.email = :email AND o.customer IS NULL")
    Optional<Order> findByIdAndEmailForGuest(@Param("orderId") Long orderId, @Param("email") String email);
}
