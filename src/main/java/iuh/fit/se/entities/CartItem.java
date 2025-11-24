package iuh.fit.se.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    Long id;

    int quantity;

    @Column(name = "unit_price")
    double unitPrice;

    double subtotal;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    Product product;

    @ManyToOne
    @JoinColumn(name = "cart_id")
//    @ToString.Exclude
    Cart cart;
}
