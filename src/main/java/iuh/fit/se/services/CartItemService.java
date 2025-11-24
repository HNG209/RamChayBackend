package iuh.fit.se.services;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.response.CartItemCreationResponse;

public interface CartItemService {
    CartItemCreationResponse createCartItem(CartItemCreationRequest request, Long customerId);
}
