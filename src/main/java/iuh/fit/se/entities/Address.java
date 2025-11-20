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
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    Long id;
    String city; // 34 tỉnh
    String ward; // phường
    String street; // đường
    @Column(name = "personal_address")
    String personalAddress; // gồm số nhà, phường

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;
}
