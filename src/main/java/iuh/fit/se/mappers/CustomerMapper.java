package iuh.fit.se.mappers;

import iuh.fit.se.dtos.response.CustomerRegistrationResponse;
import iuh.fit.se.entities.Customer;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CustomerMapper {
    CustomerRegistrationResponse toCustomerRegistrationResponse(Customer customer);
}
