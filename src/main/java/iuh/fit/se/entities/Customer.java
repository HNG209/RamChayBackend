package iuh.fit.se.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@ToString(callSuper = true, exclude = {"addresses"})
@EqualsAndHashCode(callSuper = true, exclude = {"addresses"})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "customer_id")
public class Customer extends User {

    @Column(name = "full_name")
    String fullName;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "phones", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "number", nullable = false)
    Set<String> phones;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    Set<Address> addresses;
}

