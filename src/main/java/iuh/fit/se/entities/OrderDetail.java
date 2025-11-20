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
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    Long id;

    int quantity;

    @Column(name = "unit_price")
    double unitPrice;

    // Mỗi chi tiết đơn hàng thuộc về 1 đơn hàng
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    // Mỗi chi tiết đơn hàng chứa 1 sản phẩm
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;
}
