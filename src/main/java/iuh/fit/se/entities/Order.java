package iuh.fit.se.entities;

import iuh.fit.se.entities.enums.OrderStatus;
import iuh.fit.se.entities.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    Long id;

    @CreatedDate
    @Column(updatable = false, name = "order_date")
    LocalDateTime orderDate;

    double total;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    PaymentMethod paymentMethod;

    @Column(name = "shipping_address")
    String shippingAddress;

    @Column(name = "customer_phone")
    String customerPhone;

    // Một khách hàng có thể có nhiều đơn hàng
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    // Một đơn hàng có nhiều chi tiết
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderDetail> orderDetails;

    // Thiết lập giá trị mặc định trước khi lưu vào cơ sở dữ liệu
    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        if (orderStatus == null) {
            orderStatus = OrderStatus.PENDING_PAYMENT;
        }
    }
}
