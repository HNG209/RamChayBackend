package iuh.fit.se.repositories;

import iuh.fit.se.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Tìm address trùng cho customer (để tránh lưu trùng)
     * @param customerId ID của customer
     * @param city Tên thành phố
     * @param ward Tên phường/xã
     * @param street Tên đường (có thể null)
     * @param personalAddress Số nhà, chi tiết
     * @return Address nếu tìm thấy
     */
    @Query("SELECT a FROM Address a WHERE a.customer.id = :customerId " +
           "AND a.city = :city " +
           "AND a.ward = :ward " +
           "AND (a.street = :street OR (a.street IS NULL AND :street IS NULL)) " +
           "AND a.personalAddress = :personalAddress")
    Optional<Address> findExistingAddress(
            @Param("customerId") Long customerId,
            @Param("city") String city,
            @Param("ward") String ward,
            @Param("street") String street,
            @Param("personalAddress") String personalAddress
    );

    /**
     * Lấy tất cả địa chỉ của customer
     * @param customerId ID của customer
     * @return Danh sách địa chỉ
     */
    @Query("SELECT a FROM Address a WHERE a.customer.id = :customerId ORDER BY a.id DESC")
    List<Address> findAllByCustomerId(@Param("customerId") Long customerId);
}

