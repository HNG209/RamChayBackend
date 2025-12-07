package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.request.CartItemUpdateRequest;
import iuh.fit.se.dtos.response.CartItemCreationResponse;
import iuh.fit.se.dtos.response.CartItemDeletionResponse;
import iuh.fit.se.dtos.response.GetItemsResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

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

        Customer customer = cartItem.getCart().getCustomer();
        Long ownerId = customer != null ? customer.getId() : null;

        // Nếu item đã có trong giỏ hàng của 1 user thì không được phép xoá (Integrity)
        // Nếu là khách vãng lai (ownerId = null) thì xoá là hợp lệ cho bất kì người nào
        if(ownerId != null && !ownerId.equals(customerId))
            throw new AppException(ErrorCode.OWNERSHIP_INVALID);

        cartItemRepository.deleteById(cartItem.getId());

        return CartItemDeletionResponse.builder()
                .id(cartItem.getId())
                .build();
    }

    @Override
    public CartItem persistCartItem(CartItem cartItem) {
        // Kiểm tra giỏ hàng đã có sản phẩm hay chưa
        // Nếu có thì chỉ cập nhật số lượng
        // Nếu chưa thì tạo mới

        Optional<CartItem> existingItem = cartItemRepository
                .findCartItemByProductIdAndCartId(
                        cartItem.getProduct().getId(),
                        cartItem.getCart().getId());

        if (existingItem.isPresent()) { // Cập nhật lại số lượng
            existingItem.get().setQuantity(
                    existingItem.get().getQuantity() + cartItem.getQuantity()
            );
            return cartItemRepository.save(existingItem.get());
        }

        // Tạo mới nếu chưa có
        return cartItemRepository.save(cartItem);
    }

    @Override
    public GetItemsResponse updateCartItem(Long itemId, Long customerId, CartItemUpdateRequest request) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_INVALID));

        Customer customer = item.getCart().getCustomer();
        Long ownerId = customer != null ? customer.getId() : null;

        // Nếu item đã có trong giỏ hàng của 1 user thì không được phép sửa (Integrity)
        // Nếu là khách vãng lai (ownerId = null) thì sửa là hợp lệ cho bất kì người nào
        if(ownerId != null && !ownerId.equals(customerId))
            throw new AppException(ErrorCode.OWNERSHIP_INVALID);

        item.setQuantity(request.getQuantity());
        item = cartItemRepository.save(item);
        return GetItemsResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .quantity(item.getQuantity())
                .unitPrice(item.getProduct().getPrice())
                .productName(item.getProduct().getName())
                .build();
    }

    @Override
    public Page<GetItemsResponse> getItemsByCartIdOrCustomerId(Long cartId, Long customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Lấy danh sách các món trong giỏ hàng của khách vãng lai/khách hàng đã đăng nhập
        // Ưu tiên lấy danh sách của người dùng đăng nhập trước

        if (customerId != null) {
            // Tìm giỏ hàng của khách hàng, nếu không có trả về danh sách rỗng
            Optional<Cart> customerCart = cartRepository.findCartByCustomerId(customerId);
            return customerCart.map(cart -> cartItemRepository
                    .findAllByCart_Id(cart.getId(), pageable)
                    .map(cartItem -> GetItemsResponse.builder()
                            .id(cartItem.getId())
                            .productId(cartItem.getProduct().getId())
                            .productName(cartItem.getProduct().getName())
//                            .subtotal(cartItem.getSubtotal())
                            .unitPrice(cartItem.getProduct().getPrice())
                            .quantity(cartItem.getQuantity())
                            .build())).orElseGet(Page::empty);
        }

        if (cartId != null) {
            // Nếu có cart id trong cookie thì trả về
            return cartItemRepository.findAllByCart_Id(cartId, pageable)
                    .map(cartItem -> GetItemsResponse.builder()
                            .id(cartItem.getId())
                            .productId(cartItem.getProduct().getId())
                            .productName(cartItem.getProduct().getName())
//                            .subtotal(cartItem.getSubtotal())
                            .unitPrice(cartItem.getProduct().getPrice())
                            .quantity(cartItem.getQuantity())
                            .build());
        }

        // Rỗng nếu chưa đăng nhập và chưa thêm gì vào giỏ hàng
        return Page.empty();
    }
}
