package iuh.fit.se.services;

import iuh.fit.se.dtos.request.CustomerUpdateRequest;
import iuh.fit.se.entities.Customer;

public interface CustomerService {
    // getCustomer by id return customer
     Customer getCustomerById(Long id);
     void updateCustomer(Long id, CustomerUpdateRequest request);
}
