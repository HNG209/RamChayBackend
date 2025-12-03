package iuh.fit.se.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaUploadResponse {
    Long id;
    String publicId;
    String secureUrl;
}
