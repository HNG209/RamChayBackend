package iuh.fit.se.repositories;

import iuh.fit.se.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
     @Query("SELECT c FROM CartItem c WHERE c.product.id = :productId")
     CartItem findCartItemByProductId(@Param("productId") Long productId);
}
