package iuh.fit.se.repositories;

import iuh.fit.se.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
     @Query("SELECT c FROM CartItem c WHERE c.product.id = :productId AND c.cart.id = :cartId")
     Optional<CartItem> findCartItemByProductIdAndCartId(@Param("productId") Long productId, @Param("cartId") Long cartId);

     // kiem tra cartItem co nam trong gio hang cua customer do khong, neu co thi xoa



}
