package iuh.fit.se.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePaginationResponse {
    List<RoleFindResponse> items;
    int totalPages;
    long totalElements;
    int currentPage;
    int pageSize;
    int pageNumber;
    boolean last;
    boolean first;
    int numberOfElements;
}
