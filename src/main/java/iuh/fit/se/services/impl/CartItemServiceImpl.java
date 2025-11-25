package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.response.CartItemCreationResponse;
import iuh.fit.se.entities.Cart;
import iuh.fit.se.entities.CartItem;
import iuh.fit.se.entities.Customer;
import iuh.fit.se.entities.Product;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.CartItemMapper;
import iuh.fit.se.repositories.CartItemRepository;
import iuh.fit.se.repositories.CartRepository;
import iuh.fit.se.repositories.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    @Override
    public CartItemCreationResponse createCartItem(CartItemCreationRequest request, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        Cart cart = cartRepository.findCartByCustomerId(customerId);

        // Truong hop Cart chua ton tai, tao moi
        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer); // getUser by token
            cart = cartRepository.save(cart);
        }

        //findProductById
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));


        CartItem cartItem = cartItemRepository.findCartItemByProductId(product.getId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        } else {
            cartItem.setQuantity(
                    cartItem.getQuantity() + request.getQuantity()
            );
        }

        return cartItemMapper.toCartItemCreationResponse(
                cartItemRepository.save(cartItem));
    }
}
