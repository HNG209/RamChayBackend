package iuh.fit.se.services;

import iuh.fit.se.dtos.request.AddressCreationRequest;
import iuh.fit.se.entities.Address;

import java.util.List;

public interface AddressService {
    /**
     * Lấy tất cả địa chỉ của customer
     * @param customerId ID của customer
     * @return Danh sách địa chỉ
     */
    List<Address> getAddressesByCustomerId(Long customerId);

    /**
     * Tạo địa chỉ mới cho customer
     * @param request Thông tin địa chỉ
     * @param customerId ID của customer
     * @return Address đã tạo
     */
    Address createAddress(AddressCreationRequest request, Long customerId);

    /**
     * Xóa một địa chỉ của customer
     * @param addressId ID của địa chỉ cần xóa
     * @param customerId ID của customer (để verify quyền sở hữu)
     */
    void deleteAddress(Long addressId, Long customerId);
}

