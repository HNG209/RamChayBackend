package iuh.fit.se.mappers;

import iuh.fit.se.dtos.response.OrderCreationResponse;
import iuh.fit.se.entities.Order;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface OrderMapper {
    OrderCreationResponse toOrderCreationResponse(Order order);
}
