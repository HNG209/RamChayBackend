package iuh.fit.se.controllers;

import com.nimbusds.jose.JOSEException;
import iuh.fit.se.dtos.request.CustomerRegistrationRequest;
import iuh.fit.se.dtos.request.LoginRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.CustomerRegistrationResponse;
import iuh.fit.se.dtos.response.LoginResponse;
import iuh.fit.se.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<CustomerRegistrationResponse> register(@RequestBody CustomerRegistrationRequest request) {
        return ApiResponse.<CustomerRegistrationResponse>builder()
                .result(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) throws JOSEException {
        return ApiResponse.<LoginResponse>builder()
                .result(authService.login(request))
                .build();
    }
}
