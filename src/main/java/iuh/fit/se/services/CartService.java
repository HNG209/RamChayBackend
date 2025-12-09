package iuh.fit.se.services;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.response.AddCartItemResponse;
import iuh.fit.se.entities.Product;

public interface CartService {

    AddCartItemResponse addItem(CartItemCreationRequest request, Long userId, Long cartId);

    void removeItem(Product product);

    void updateItem(Product product, int quantity);

    double calculate();


}
