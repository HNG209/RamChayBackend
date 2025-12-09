package iuh.fit.se.mappers;

import iuh.fit.se.dtos.response.CustomerInOrderResponse;
import iuh.fit.se.dtos.response.OrderCreationResponse;
import iuh.fit.se.dtos.response.OrderDetailResponse;
import iuh.fit.se.dtos.response.ProductInOrderResponse;
import iuh.fit.se.entities.Customer;
import iuh.fit.se.entities.Order;
import iuh.fit.se.entities.OrderDetail;
import iuh.fit.se.entities.Product;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface OrderMapper {
    OrderCreationResponse toOrderCreationResponse(Order order);

    CustomerInOrderResponse toCustomerInOrderResponse(Customer customer);

    @Mapping(target = "subtotal", expression = "java(orderDetail.getQuantity() * orderDetail.getUnitPrice())")
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

    ProductInOrderResponse toProductInOrderResponse(Product product);
}
