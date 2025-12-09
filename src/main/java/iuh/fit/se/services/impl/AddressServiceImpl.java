package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.request.AddressCreationRequest;
import iuh.fit.se.entities.Address;
import iuh.fit.se.entities.Customer;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.repositories.AddressRepository;
import iuh.fit.se.repositories.CustomerRepository;
import iuh.fit.se.services.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    /**
     * Lấy tất cả địa chỉ của customer
     * @param customerId ID của customer
     * @return Danh sách địa chỉ
     */
    @Override
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public List<Address> getAddressesByCustomerId(Long customerId) {
        List<Address> addresses = addressRepository.findAllByCustomerId(customerId);
        log.info("Retrieved {} addresses for customer {}", addresses.size(), customerId);
        return addresses;
    }

    /**
     * Tạo địa chỉ mới cho customer (không lưu trùng)
     * @param request Thông tin địa chỉ
     * @param customerId ID của customer
     * @return Address đã tạo hoặc address đã tồn tại
     */
    @Override
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Transactional
    public Address createAddress(AddressCreationRequest request, Long customerId) {
        // Check xem địa chỉ này đã tồn tại chưa
        Optional<Address> existingAddress = addressRepository.findExistingAddress(
                customerId,
                request.getCity(),
                request.getWard(),
                request.getStreet(),
                request.getPersonalAddress()
        );

        if (existingAddress.isPresent()) {
            log.info("Address already exists for customer {}, returning existing", customerId);
            return existingAddress.get();
        }

        // Địa chỉ chưa có, tạo mới
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        Address newAddress = Address.builder()
                .city(request.getCity())
                .ward(request.getWard())
                .street(request.getStreet())
                .personalAddress(request.getPersonalAddress())
                .customer(customer)
                .build();

        Address savedAddress = addressRepository.save(newAddress);
        log.info("Created new address {} for customer {}", savedAddress.getId(), customerId);
        return savedAddress;
    }

    /**
     * Xóa một địa chỉ của customer
     * Chỉ cho phép xóa nếu địa chỉ thuộc về customer đó
     * @param addressId ID của địa chỉ cần xóa
     * @param customerId ID của customer (để verify quyền sở hữu)
     */
    @Override
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Transactional
    public void deleteAddress(Long addressId, Long customerId) {
        // Tìm address và verify quyền sở hữu
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        // Kiểm tra xem address có thuộc về customer này không
        if (!address.getCustomer().getId().equals(customerId)) {
            log.warn("Customer {} attempted to delete address {} that doesn't belong to them",
                    customerId, addressId);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Xóa address
        addressRepository.delete(address);
        log.info("Deleted address {} for customer {}", addressId, customerId);
    }
}

