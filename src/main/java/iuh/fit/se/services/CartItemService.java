package iuh.fit.se.services;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.response.CartItemCreationResponse;
import iuh.fit.se.dtos.response.CartItemDeletionResponse;

public interface CartItemService {
    CartItemCreationResponse createCartItem(CartItemCreationRequest request, Long customerId);

    CartItemDeletionResponse deleteCartItem(Long cartItemId, Long customerId);
}
