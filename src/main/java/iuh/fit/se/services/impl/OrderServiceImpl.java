package iuh.fit.se.services.impl;
import iuh.fit.se.dtos.request.OrderCreationRequest;
import iuh.fit.se.dtos.response.OrderCreationResponse;
import iuh.fit.se.entities.*;
import iuh.fit.se.entities.enums.OrderStatus;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mappers.OrderMapper;
import iuh.fit.se.repositories.CartItemRepository;
import iuh.fit.se.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements iuh.fit.se.services.OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartItemRepository cartItemRepository;
    private final iuh.fit.se.repositories.CustomerRepository customerRepository;
    private final iuh.fit.se.services.EmailService emailService;
    private final iuh.fit.se.repositories.AddressRepository addressRepository;

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
        // 1. Validate: nếu là khách vãng lai thì email là bắt buộc
        if (customerId == null) {
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new AppException(ErrorCode.EMAIL_INVALID);
            }
            if (!isValidEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_INVALID);
            }
        }

        // 2. Validate: nếu có customerId thì phải có trong DB
        Customer customer = null;
        if (customerId != null) {
            customer = Customer.builder().id(customerId).build();
        }

        // 3. Validate items không rỗng
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        // 4. Xác định địa chỉ giao hàng
        String shippingAddressString;
        if (request.getAddressId() != null) {
            // User chọn địa chỉ đã lưu
            Address address = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

            // Verify ownership (nếu là khách đã đăng nhập)
            if (customerId != null && !address.getCustomer().getId().equals(customerId)) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            shippingAddressString = address.getFullAddress();
        } else if (request.getShippingAddress() != null && !request.getShippingAddress().isEmpty()) {
            // User nhập địa chỉ mới dạng string
            shippingAddressString = request.getShippingAddress();
        } else {
            // Không có addressId và không có shippingAddress
            throw new AppException(ErrorCode.FIELD_BLANK);
        }

        // 5. Tạo Order
        Order order = Order.builder()
                .customer(customer)
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .shippingAddress(shippingAddressString) // Chuỗi địa chỉ đầy đủ
                .paymentMethod(request.getPaymentMethod())
                .email(request.getEmail()) // Lưu email để gửi xác nhận
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .build();

        // 5. Tạo OrderDetail từ các items được chọn
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

        // 6. Set orderDetails và total cho order
        order.setOrderDetails(orderDetails);
        order.setTotal(total);

        // 7. Lưu order (cascade sẽ lưu orderDetails)
        Order savedOrder = orderRepository.save(order);

        // 8. Xóa các CartItem đã checkout hoàn toàn
        if (!cartItemsToDelete.isEmpty()) {
            cartItemRepository.deleteAll(cartItemsToDelete);
        }

        log.info("Order created successfully. OrderId: {}, Total: {}, Items: {}, Customer: {}",
                savedOrder.getId(), savedOrder.getTotal(), orderDetails.size(),
                customerId != null ? customerId : "Guest");

        // 9. Gửi email xác nhận đơn hàng
        String customerEmail = savedOrder.getEmail();

        // Nếu không có email trong request nhưng là khách đã đăng nhập, lấy từ database
        if ((customerEmail == null || customerEmail.isEmpty()) && customerId != null) {
            Customer fullCustomer = customerRepository.findById(customerId).orElse(null);
            if (fullCustomer != null) {
                // Lấy email từ user entity (luôn có vì NOT NULL)
                customerEmail = fullCustomer.getEmail();
                savedOrder.setEmail(customerEmail);
                orderRepository.save(savedOrder);
                log.info("Using customer email from database: {}", customerEmail);
            }
        }

        // Gửi email nếu có địa chỉ email hợp lệ
        if (customerEmail != null && !customerEmail.isEmpty() && isValidEmail(customerEmail)) {
            try {
                emailService.sendOrderConfirmationEmail(savedOrder, customerEmail);
                log.info("Order confirmation email queued for {}", customerEmail);
            } catch (Exception e) {
                log.error("Failed to queue order confirmation email: {}", e.getMessage());
                // Không throw exception để không ảnh hưởng đến việc tạo đơn hàng
            }
        } else {
            log.warn("No valid email address provided for order {}. Email will not be sent.", savedOrder.getId());
        }

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

    /**
     * Lấy chi tiết đơn hàng cho khách vãng lai dựa trên orderId và email.
     * Không yêu cầu authentication.
     * @param orderId
     * @param email
     * @return
     * @author Duc
     * @date 12/09/2024
     */
    @Override
    public OrderCreationResponse getGuestOrderById(Long orderId, String email) {
        if (email == null || email.isEmpty()) {
            throw new AppException(ErrorCode.EMAIL_INVALID);
        }

        Order order = orderRepository.findByIdAndEmailForGuest(orderId, email.toLowerCase().trim())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        log.info("Retrieved guest order {} with email {}", orderId, email);
        return orderMapper.toOrderCreationResponse(order);
    }

    /**
     * Validate email format using simple regex
     * @param email
     * @return true if email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Simple email validation: contains @ and . after @
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

}
