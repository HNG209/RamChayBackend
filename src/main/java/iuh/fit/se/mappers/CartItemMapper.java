package iuh.fit.se.mappers;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.response.CartItemCreationResponse;
import iuh.fit.se.entities.CartItem;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CartItemMapper {

    CartItemCreationResponse toCartItemCreationResponse(CartItem cartItem);
}
