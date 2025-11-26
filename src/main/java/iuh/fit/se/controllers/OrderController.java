package iuh.fit.se.controllers;

import iuh.fit.se.dtos.request.OrderCreationRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.OrderCreationResponse;
import iuh.fit.se.services.OrderService;
import lombok.RequiredArgsConstructor;
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
     * Tạo đơn hàng mới từ giỏ hàng của khách hàng hiện tại.
     * @param request
     * @param jwt
     * @return
     * @author Duc
     * @date 11/26/2025     */
    @PostMapping
    public ApiResponse<OrderCreationResponse> createOrder(@RequestBody OrderCreationRequest request,
                                                          @AuthenticationPrincipal Jwt jwt) {

        Long customerId = Long.valueOf(jwt.getSubject());
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

}
