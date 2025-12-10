package iuh.fit.se.controllers;

import iuh.fit.se.dtos.request.OrderCreationRequest;
import iuh.fit.se.dtos.request.OrderStatusUpdateRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.OrderCreationResponse;
import iuh.fit.se.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * Tạo đơn hàng mới từ các items được chọn trong giỏ hàng.
     * Hỗ trợ cả khách hàng đăng nhập và khách vãng lai.
     * - Nếu có JWT: customerId lấy từ JWT, lấy items từ cart của customer
     * - Nếu không có JWT: khách vãng lai, phải cung cấp đầy đủ thông tin người nhận
     * @param request Thông tin đơn hàng
     * @param authentication Authentication object (có thể null cho khách vãng lai)
     * @return OrderCreationResponse
     * @author Duc
     * @date 12/01/2024
     */
    @PostMapping
    public ApiResponse<OrderCreationResponse> createOrder(@RequestBody OrderCreationRequest request,
                                                          Authentication authentication) {
        // Lấy customerId từ JWT nếu user đã đăng nhập, null nếu là khách vãng lai
        Long customerId = null;
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            customerId = Long.valueOf(jwt.getSubject());
        }

        return ApiResponse.<OrderCreationResponse>builder()
                .result(orderService.createOrder(request, customerId))
                .build();
    }

    /**
     * Lấy danh sách đơn hàng của khách hàng hiện tại.
     * @param jwt
     * @return
     * @author Duc
     * @date 11/26/2025     */
    @GetMapping
    public ApiResponse<List<OrderCreationResponse>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        Long customerId = Long.valueOf(jwt.getSubject());
        return ApiResponse.<List<OrderCreationResponse>>builder()
                .result(orderService.getOrdersByCustomerId(customerId))
                .build();
    }

    /**
     * Lấy chi tiết đơn hàng theo ID cho khách hàng hiện tại.
     * @param orderId
     * @param jwt
     * @return
     * @author Duc
     * @date 11/26/2025     */
    @GetMapping("/{orderId}")
    public ApiResponse<OrderCreationResponse> getOrderById(@PathVariable Long orderId,
                                                           @AuthenticationPrincipal Jwt jwt) {
        Long customerId = Long.valueOf(jwt.getSubject());
        return ApiResponse.<OrderCreationResponse>builder()
                .result(orderService.getOrderById(orderId, customerId))
                .build();
    }

    /**
     * Lấy chi tiết đơn hàng cho khách vãng lai bằng orderId và email.
     * Endpoint này không yêu cầu authentication.
     * @param orderId
     * @param email
     * @return
     * @author Duc
     * @date 12/09/2024
     */
    @GetMapping("/{orderId}/guest")
    public ApiResponse<OrderCreationResponse> getGuestOrderById(@PathVariable Long orderId,
                                                                @RequestParam String email) {
        return ApiResponse.<OrderCreationResponse>builder()
                .result(orderService.getGuestOrderById(orderId, email))
                .build();
    }

    // ==================== MANAGER ENDPOINTS ====================

    /**
     * Lấy tất cả đơn hàng trong hệ thống (chỉ dành cho manager).
     * Yêu cầu permission VIEW_ALL_ORDERS.
     * @return Danh sách tất cả đơn hàng
     * @author Duc
     * @date 12/10/2025
     */
    @GetMapping("/manager/all")
    public ApiResponse<List<OrderCreationResponse>> getAllOrdersForManager() {
        return ApiResponse.<List<OrderCreationResponse>>builder()
                .result(orderService.getAllOrders())
                .build();
    }

    /**
     * Lấy chi tiết đơn hàng theo ID (chỉ dành cho manager).
     * Manager có thể xem bất kỳ đơn hàng nào.
     * Yêu cầu permission VIEW_ALL_ORDERS.
     * @param orderId ID của đơn hàng
     * @return Chi tiết đơn hàng
     * @author Duc
     * @date 12/10/2025
     */
    @GetMapping("/manager/{orderId}")
    public ApiResponse<OrderCreationResponse> getOrderByIdForManager(@PathVariable Long orderId) {
        return ApiResponse.<OrderCreationResponse>builder()
                .result(orderService.getOrderByIdForManager(orderId))
                .build();
    }

    /**
     * Cập nhật trạng thái đơn hàng (chỉ dành cho manager).
     * Yêu cầu permission UPDATE_ORDER_STATUS.
     * @param orderId ID của đơn hàng cần cập nhật
     * @param request Thông tin trạng thái mới
     * @return Đơn hàng sau khi cập nhật
     * @author Duc
     * @date 12/10/2025
     */
    @PutMapping("/manager/{orderId}/status")
    public ApiResponse<OrderCreationResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ApiResponse.<OrderCreationResponse>builder()
                .result(orderService.updateOrderStatus(orderId, request))
                .build();
    }

}
