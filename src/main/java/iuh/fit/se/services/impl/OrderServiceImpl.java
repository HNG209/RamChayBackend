package iuh.fit.se.services.impl;
import iuh.fit.se.dtos.request.OrderCreationRequest;
import iuh.fit.se.dtos.response.OrderCreationResponse;
import iuh.fit.se.entities.*;
import iuh.fit.se.entities.enums.OrderStatus;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.OrderMapper;
import iuh.fit.se.repositories.CartItemRepository;
import iuh.fit.se.repositories.CartRepository;
import iuh.fit.se.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements iuh.fit.se.services.OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Tạo đơn hàng mới từ giỏ hàng của khách hàng. Sau khi tạo đơn hàng thành công, các mục trong giỏ hàng sẽ bị xóa.
     * Trả về DTO của đơn hàng đã tạo để frontend hiển thị.
     * @param request
     * @param customerId
     * @return
     * @author Duc
     * @date 11/26/2025     */
    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public OrderCreationResponse createOrder(OrderCreationRequest request, Long customerId)
    {
        // 1. Lấy Cart của customer
        Cart cart = cartRepository.findCartByCustomerId(customerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        // 2. Lấy danh sách CartItem
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        // 3. Tạo Order
        Order order = Order.builder()
                .customer(Customer.builder().id(customerId).build())
                .shippingAddress(request.getShippingAddress())
                .customerPhone(request.getCustomerPhone())
                .paymentMethod(request.getPaymentMethod())
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .build();

        // 4. Tạo OrderDetail từ CartItem
        List<OrderDetail> orderDetails = new ArrayList<>();
        double total = 0.0;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Validate product tồn tại và còn hàng
            if (product == null) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            if (product.getStock() < cartItem.getQuantity()) {
                log.error("Product {} is out of stock. Available: {}, Requested: {}",
                    product.getName(), product.getStock(), cartItem.getQuantity());
                throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }

            // Tạo OrderDetail
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice()) // Lấy giá hiện tại của product
                    .build();

            orderDetails.add(orderDetail);

            // Tính tổng tiền
            total += orderDetail.getQuantity() * orderDetail.getUnitPrice();

            // Giảm số lượng sản phẩm trong kho
            product.setStock(product.getStock() - cartItem.getQuantity());
        }

        // 5. Set orderDetails và total cho order
        order.setOrderDetails(orderDetails);
        order.setTotal(total);

        // 6. Lưu order (cascade sẽ lưu orderDetails)
        Order savedOrder = orderRepository.save(order);

        // 7. Xóa các CartItem đã checkout
        cartItemRepository.deleteAll(cartItems);

        log.info("Order created successfully. OrderId: {}, Total: {}, Items: {}",
            savedOrder.getId(), savedOrder.getTotal(), orderDetails.size());

        return orderMapper.toOrderCreationResponse(savedOrder);
    }

    /**
     * Lấy tất cả đơn hàng của một khách hàng cụ thể và trả về dạng DTO để frontend hiển thị.
     * @param customerId
     * @return
     * @author Duc
     * @date 11/26/2025     */
    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<OrderCreationResponse> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findAllByCustomerId(customerId);
        log.info("Retrieved {} orders for customer {}", orders.size(), customerId);
        return orders.stream()
                .map(orderMapper::toOrderCreationResponse)
                .toList();
    }

    /**
     * Lấy chi tiết một đơn hàng cụ thể của khách hàng dựa trên orderId và customerId. Trả về dạng DTO để frontend hiển thị.
     * @param orderId
     * @param customerId
     * @return
     * @author Duc
     * @date 11/26/2025     */
    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderCreationResponse getOrderById(Long orderId, Long customerId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        log.info("Retrieved order {} for customer {}", orderId, customerId);
        return orderMapper.toOrderCreationResponse(order);
    }


}
