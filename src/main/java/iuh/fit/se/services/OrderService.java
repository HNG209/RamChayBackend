package iuh.fit.se.services;

import iuh.fit.se.dtos.request.OrderCreationRequest;
import iuh.fit.se.dtos.response.OrderCreationResponse;

import java.util.List;

public interface OrderService {
    OrderCreationResponse createOrder(OrderCreationRequest request, Long customerId);
    List<OrderCreationResponse> getOrdersByCustomerId(Long customerId);
    OrderCreationResponse getOrderById(Long orderId, Long customerId);
    OrderCreationResponse getGuestOrderById(Long orderId, String email);
}
