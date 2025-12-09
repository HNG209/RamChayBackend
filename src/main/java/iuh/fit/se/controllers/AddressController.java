package iuh.fit.se.controllers;

import iuh.fit.se.dtos.request.AddressCreationRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.entities.Address;
import iuh.fit.se.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    /**
     * Get all addresses of current customer
     */
    @GetMapping
    public ApiResponse<List<Address>> getMyAddresses(@AuthenticationPrincipal Jwt jwt) {
        Long customerId = Long.valueOf(jwt.getSubject());
        return ApiResponse.<List<Address>>builder()
                .result(addressService.getAddressesByCustomerId(customerId))
                .build();
    }

    /**
     * Create new address for current customer
     */
    @PostMapping
    public ApiResponse<Address> createAddress(
            @Valid @RequestBody AddressCreationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long customerId = Long.valueOf(jwt.getSubject());
        return ApiResponse.<Address>builder()
                .result(addressService.createAddress(request, customerId))
                .build();
    }

    /**
     * Delete an address
     */
    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> deleteAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal Jwt jwt) {
        Long customerId = Long.valueOf(jwt.getSubject());
        addressService.deleteAddress(addressId, customerId);
        return ApiResponse.<Void>builder()
                .message("Address deleted successfully")
                .build();
    }
}


