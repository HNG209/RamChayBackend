package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.response.AddCartItemResponse;
import iuh.fit.se.entities.Cart;
import iuh.fit.se.entities.CartItem;
import iuh.fit.se.entities.Customer;
import iuh.fit.se.entities.Product;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.repositories.CartItemRepository;
import iuh.fit.se.repositories.CartRepository;
import iuh.fit.se.services.CartItemService;
import iuh.fit.se.services.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemService cartItemService;

    @Override
    @Transactional
    public AddCartItemResponse addItem(CartItemCreationRequest request, Long userId, Long cartId) {
        // quan trọng nhất, có 2 luồng cho khách hàng đã đăng nhập và khách vãng lai (4 nhánh điều kiện)
        if (userId == null) { // nếu người dùng không đăng nhập
            Optional<Cart> existingCart =
                    cartId == null
                            ? Optional.empty()
                            : cartRepository.findById(cartId);

            // Chưa đăng nhập và cookies chứa giỏ hàng chưa tồn tại
            if (existingCart.isEmpty()) {
                Cart newCart = new Cart();
                newCart.setCustomer(null);

                CartItem item = CartItem.builder()
                        .cart(newCart)
//                        .unitPrice(request.getUnitPrice())
                        .product(Product.builder().id(request.getProductId()).build())
                        .quantity(request.getQuantity())
//                        .subtotal(request.getSubtotal())
                        .build();

                newCart.setCartItems(List.of(item));
                cartRepository.save(newCart);

                return AddCartItemResponse.builder()
                        .cartId(newCart.getId())
                        .build();
            }

            // Kiểm tra giỏ hàng đó có phải của người khác hay không (Integrity check)
            if (existingCart.get().getCustomer() != null)
                throw new AppException(ErrorCode.OWNERSHIP_INVALID);

            // Chưa đăng nhập nhưng còn GIỮ cookies giỏ hàng
            CartItem item = CartItem.builder()
                    .cart(existingCart.get())
//                    .unitPrice(request.getUnitPrice())
                    .product(Product.builder().id(request.getProductId()).build())
                    .quantity(request.getQuantity())
//                    .subtotal(request.getSubtotal())
                    .build();

            cartItemService.persistCartItem(item); // sửa hoặc tạo nếu chưa có

            return AddCartItemResponse.builder()
                    .cartId(existingCart.get().getId())
                    .build();
        }

        Optional<Cart> existingCustomerCart = cartRepository.findCartByCustomerId(userId);
        // Đã đăng nhập nhưng chưa có cart
        if (existingCustomerCart.isEmpty()) {
            Cart newCart = new Cart();
            newCart.setCustomer(Customer.builder().id(userId).build());

            CartItem newItem = CartItem.builder()
                    .cart(newCart)
//                    .unitPrice(request.getUnitPrice())
                    .product(Product.builder().id(request.getProductId()).build())
                    .quantity(request.getQuantity())
//                    .subtotal(request.getSubtotal())
                    .build();

            newCart.setCartItems(List.of(newItem));

            cartRepository.save(newCart);

            return AddCartItemResponse.builder()
                    .cartId(null)
                    .build();
        }

        // Đã đăng nhập và đã có cart
        CartItem newItem = CartItem.builder()
                .cart(existingCustomerCart.get())
//                .unitPrice(request.getUnitPrice())
                .product(Product.builder().id(request.getProductId()).build())
                .quantity(request.getQuantity())
//                .subtotal(request.getSubtotal())
                .build();

        cartItemService.persistCartItem(newItem); // sửa hoặc tạo nếu chưa có

        return AddCartItemResponse.builder()
                .cartId(null)
                .build();
    }

    @Override
    public void removeItem(Product product) {

    }

    @Override
    public void updateItem(Product product, int quantity) {

    }

    @Override
    public double calculate() {
        return 0;
    }
}
