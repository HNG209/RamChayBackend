package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.response.CartItemCreationResponse;
import iuh.fit.se.entities.Cart;
import iuh.fit.se.entities.CartItem;
import iuh.fit.se.entities.Product;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.CartItemMapper;
import iuh.fit.se.repositories.CartItemRepository;
import iuh.fit.se.repositories.CartRepository;
import iuh.fit.se.repositories.ProductRepository;
import iuh.fit.se.services.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartItemCreationResponse createCartItem(CartItemCreationRequest request) {
        System.out.println("Cart Item created");
        Cart cart = null;

        if (cartRepository.findById(request.getCartId()).isPresent()) {
            cart = new Cart();
            cart.setCustomer(null); // getUser by token
            cartRepository.save(cart);
        }

        //findProductById
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(request.getQuantity());

        return cartItemMapper.toCartItemCreationResponse(
                cartItemRepository.save(cartItem));
    }
}
