package iuh.fit.se.dtos.response;


import iuh.fit.se.dtos.request.ManagerFindRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManagerPaginationResponse {

    List<ManagerFindResponse> items;
    int totalPages;
    long totalElements;
    int currentPage;
    int pageSize;
    int pageNumber;
    boolean last;
    boolean first;
    int  numberOfElements;
}
