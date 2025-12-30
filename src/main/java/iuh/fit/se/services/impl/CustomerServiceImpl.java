package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.CustomerUpdateRequest;
import iuh.fit.se.entities.Address;
import iuh.fit.se.entities.Customer;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.repositories.CustomerRepository;
import iuh.fit.se.services.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.getCustomerById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = getCustomerById(id);

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhones(request.getPhones());

        customer.getAddresses().clear();

        for (Address address : request.getAddresses()) {
            address.setId(null);
            address.setCustomer(customer);
            customer.getAddresses().add(address);
        }

        customerRepository.save(customer);
    }
}
