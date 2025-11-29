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
@Table(name = "media_files")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    Long id;

    @Column(name = "public_id")
    String publicId;

    @Column(name = "secure_url")
    String secureUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;
}
