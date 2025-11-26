package iuh.fit.se.controllers;

import iuh.fit.se.dtos.request.OrderCreationRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.OrderCreationResponse;
import iuh.fit.se.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderCreationResponse> addProduct(@RequestBody OrderCreationRequest request,
                                                         @AuthenticationPrincipal Jwt jwt) {

        Long customerId = Long.valueOf(jwt.getSubject());
        return ApiResponse.<OrderCreationResponse>builder()
                .result(orderService.createOrder(request, customerId))
                .build();
    }

}
