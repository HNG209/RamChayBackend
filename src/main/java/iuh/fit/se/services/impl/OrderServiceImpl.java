package iuh.fit.se.services.impl;
import iuh.fit.se.dtos.request.OrderCreationRequest;
import iuh.fit.se.dtos.response.OrderCreationResponse;
import iuh.fit.se.entities.Customer;
import iuh.fit.se.entities.Order;
import iuh.fit.se.mappers.OrderMapper;
import iuh.fit.se.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements iuh.fit.se.services.OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderCreationResponse createOrder(OrderCreationRequest request, Long customerId)
    {
        Order order = Order.builder()
                .customer(Customer.builder().id(customerId).build())
                .shippingAddress(request.getShippingAddress())
                .customerPhone(request.getCustomerPhone())
                .paymentMethod(request.getPaymentMethod())
                .build();

        return orderMapper.toOrderCreationResponse(orderRepository.save(order));
    }


}
