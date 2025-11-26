package iuh.fit.se.services;

import iuh.fit.se.dtos.request.OrderCreationRequest;
import iuh.fit.se.dtos.response.OrderCreationResponse;

public interface OrderService {
    OrderCreationResponse createOrder(OrderCreationRequest request, Long customerId);
}
