package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.response.CartItemCreationResponse;
import iuh.fit.se.dtos.response.CartItemDeletionResponse;
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
import org.springframework.transaction.annotation.Transactional;

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

        // Dùng Optional để tìm cart, nếu không có thì tạo mới
        Cart cart = cartRepository.findCartByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });


        //findProductById
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        CartItem cartItem = cartItemRepository
                .findCartItemByProductIdAndCartId(product.getId(), cart.getId())
                .map(item -> {
                    // Nếu đã có => cập nhật quantity
                    item.setQuantity(item.getQuantity() + request.getQuantity());
                    return item;
                })
                .orElseGet(() -> {
                    // Nếu chưa có => tạo mới
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(request.getQuantity());
                    return newItem;
                });

        return cartItemMapper.toCartItemCreationResponse(
                cartItemRepository.save(cartItem));
    }

    @Override
    @Transactional
    public CartItemDeletionResponse deleteCartItem(Long cartItemId, Long customerId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!cartItem.getCart().getCustomer().getId().equals(customerId))
            throw new AppException(ErrorCode.CART_ITEM_INVALID);

        cartItemRepository.deleteById(cartItem.getId());

        return CartItemDeletionResponse.builder()
                .id(cartItem.getId())
                .build();
    }
}
