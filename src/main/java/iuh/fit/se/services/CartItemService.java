package iuh.fit.se.services;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.request.CartItemUpdateRequest;
import iuh.fit.se.dtos.response.CartItemCreationResponse;
import iuh.fit.se.dtos.response.CartItemDeletionResponse;
import iuh.fit.se.dtos.response.GetItemsResponse;
import iuh.fit.se.entities.CartItem;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface CartItemService {
    CartItemCreationResponse createCartItem(CartItemCreationRequest request, Long customerId);

    CartItemDeletionResponse deleteCartItem(Long cartItemId, Long customerId);

    CartItem persistCartItem(CartItem cartItem);

    GetItemsResponse updateCartItem(Long itemId, Long customerId, CartItemUpdateRequest request);

    Page<GetItemsResponse> getItemsByCartIdOrCustomerId(Long cartId, Long customerId, int page, int size);
}
