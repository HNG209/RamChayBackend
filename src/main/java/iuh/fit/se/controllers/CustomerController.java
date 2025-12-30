package iuh.fit.se.controllers;

import iuh.fit.se.dtos.request.CustomerUpdateRequest;
import iuh.fit.se.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PutMapping
    public ResponseEntity<Void> updateMyProfile(
            @RequestBody CustomerUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long id = Long.valueOf(jwt.getSubject());
        customerService.updateCustomer(id, request);
        return ResponseEntity.noContent().build(); // 204
    }

}
