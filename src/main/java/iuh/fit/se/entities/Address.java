package iuh.fit.se.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore // Ngăn circular reference: Customer -> Address -> Customer
    Customer customer;

    /**
     * Tạo chuỗi địa chỉ đầy đủ từ các thành phần
     * @return Địa chỉ đầy đủ dạng: "123 ABC, Đường XYZ, Phường 1, TP.HCM"
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();

        if (personalAddress != null && !personalAddress.isEmpty()) {
            sb.append(personalAddress);
        }

        if (street != null && !street.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(street);
        }

        if (ward != null && !ward.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ward);
        }

        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }

        return sb.toString();
    }

    /**
     * Check if this address equals another address (ignore id and customer)
     * @param other Address to compare
     * @return true if all fields match
     */
    public boolean isSameAddress(Address other) {
        if (other == null) return false;

        return java.util.Objects.equals(this.city, other.city) &&
               java.util.Objects.equals(this.ward, other.ward) &&
               java.util.Objects.equals(this.street, other.street) &&
               java.util.Objects.equals(this.personalAddress, other.personalAddress);
    }
}
