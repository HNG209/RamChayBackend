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
    private final CartItemRepository cartItemRepository;

    /**
     * Tạo đơn hàng mới từ các items được chọn trong giỏ hàng.
     * Sau khi tạo đơn hàng thành công, các mục đã chọn sẽ bị xóa khỏi giỏ hàng.
     * Hỗ trợ cả khách hàng đăng nhập và khách vãng lai.
     * @param request
     * @param customerId - Có thể null cho khách vãng lai
     * @return
     * @author Duc
     * @date 12/01/2024
     */
    // Bỏ PreAuthorize để hỗ trợ cả khách vãng lai
    @Override
    @Transactional
    public OrderCreationResponse createOrder(OrderCreationRequest request, Long customerId)
    {
        // 1. Validate: nếu có customerId thì phải có trong DB
        Customer customer = null;
        if (customerId != null) {
            customer = Customer.builder().id(customerId).build();
        }

        // 2. Validate items không rỗng
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        // 3. Tạo Order
        Order order = Order.builder()
                .customer(customer)
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .build();

        // 4. Tạo OrderDetail từ các items được chọn
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<CartItem> cartItemsToDelete = new ArrayList<>();
        double total = 0.0;

        for (var itemRequest : request.getItems()) {
            // Lấy CartItem theo ID
            CartItem cartItem = cartItemRepository.findById(itemRequest.getCartItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

            Product product = cartItem.getProduct();

            // Validate product tồn tại
            if (product == null) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            // Validate số lượng yêu cầu không vượt quá số lượng trong cart
            if (itemRequest.getQuantity() > cartItem.getQuantity()) {
                log.error("Requested quantity {} exceeds cart quantity {} for product {}",
                        itemRequest.getQuantity(), cartItem.getQuantity(), product.getName());
                throw new AppException(ErrorCode.INVALID_QUANTITY);
            }

            // Validate số lượng tồn kho
            if (product.getStock() < itemRequest.getQuantity()) {
                log.error("Product {} is out of stock. Available: {}, Requested: {}",
                        product.getName(), product.getStock(), itemRequest.getQuantity());
                throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }

            // Tạo OrderDetail
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice()) // Lấy giá hiện tại của product
                    .build();

            orderDetails.add(orderDetail);

            // Tính tổng tiền
            total += orderDetail.getQuantity() * orderDetail.getUnitPrice();

            // Giảm số lượng sản phẩm trong kho
            product.setStock(product.getStock() - itemRequest.getQuantity());

            // Cập nhật hoặc xóa CartItem
            if (itemRequest.getQuantity().equals(cartItem.getQuantity())) {
                // Nếu đặt hết số lượng trong cart thì xóa cart item
                cartItemsToDelete.add(cartItem);
            } else {
                // Nếu đặt một phần thì giảm số lượng trong cart
                cartItem.setQuantity(cartItem.getQuantity() - itemRequest.getQuantity());
            }
        }

        // 5. Set orderDetails và total cho order
        order.setOrderDetails(orderDetails);
        order.setTotal(total);

        // 6. Lưu order (cascade sẽ lưu orderDetails)
        Order savedOrder = orderRepository.save(order);

        // 7. Xóa các CartItem đã checkout hoàn toàn
        if (!cartItemsToDelete.isEmpty()) {
            cartItemRepository.deleteAll(cartItemsToDelete);
        }

        log.info("Order created successfully. OrderId: {}, Total: {}, Items: {}, Customer: {}",
                savedOrder.getId(), savedOrder.getTotal(), orderDetails.size(),
                customerId != null ? customerId : "Guest");

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
