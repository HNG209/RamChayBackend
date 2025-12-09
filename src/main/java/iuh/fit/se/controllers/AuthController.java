package iuh.fit.se.controllers;

import com.nimbusds.jose.JOSEException;
import iuh.fit.se.dtos.request.CustomerRegistrationRequest;
import iuh.fit.se.dtos.request.LoginRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.CustomerRegistrationResponse;
import iuh.fit.se.dtos.response.LoginResponse;
import iuh.fit.se.dtos.response.MyProfileResponse;
import iuh.fit.se.entities.Customer;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<CustomerRegistrationResponse> register(@Valid @RequestBody CustomerRegistrationRequest request) {
        return ApiResponse.<CustomerRegistrationResponse>builder()
                .result(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            @CookieValue(name = "cart", required = false) String cartId) throws JOSEException {
        LoginResponse loginResponse = authService.login(request,
                cartId == null ? null : Long.valueOf(cartId));

        String refreshToken = loginResponse.getRefreshToken();
        String accessToken = loginResponse.getAccessToken();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)  // Quan trọng: JS không đọc được
                .secure(false)   // True nếu chạy https (Production), False nếu chạy localhost
                .path("/")       // Cookie có hiệu lực toàn server
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax") // Hoặc "None" nếu khác domain, "Lax" nếu cùng domain/subdomain
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        // Xoá cart cookie nếu có
        ResponseCookie deleteCart = ResponseCookie.from("cart", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCart.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(ApiResponse.<LoginResponse>builder()
                        .result(loginResponse)
                        .build());
    }

    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(
            @RequestBody LoginRequest request) throws JOSEException {
        LoginResponse loginResponse = authService.adminLogin(request);

        String refreshToken = loginResponse.getRefreshToken();
        String accessToken = loginResponse.getAccessToken();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)  // Quan trọng: JS không đọc được
                .secure(false)   // True nếu chạy https (Production), False nếu chạy localhost
                .path("/")       // Cookie có hiệu lực toàn server
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax") // Hoặc "None" nếu khác domain, "Lax" nếu cùng domain/subdomain
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(ApiResponse.<LoginResponse>builder()
                        .result(loginResponse)
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Tạo cookie rỗng với thời gian sống = 0 để xóa
        ResponseCookie deleteAccess = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie deleteRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .body(new ApiResponse(1000, "Đăng xuất thành công", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(
            // Lấy cookie refreshToken tự động
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) throws ParseException, JOSEException {

        // 1. Kiểm tra nếu không có cookie gửi lên
        if (refreshToken == null) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        // 2. Gọi Service để lấy token mới
        LoginResponse result = authService.refreshToken(refreshToken);

        // 3. Tạo Cookie Access Token mới (Ghi đè cái cũ)
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", result.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        // 4. Tạo Cookie Refresh Token mới (Ghi đè cái cũ)
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        // 5. Trả về Response (Body rỗng, quan trọng là Header Set-Cookie)
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.<Void>builder()
                        .message("Refresh token thành công")
                        .build());
    }

    @GetMapping("/me")
    public ApiResponse<MyProfileResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        Long id = Long.valueOf(jwt.getSubject());

        return ApiResponse.<MyProfileResponse>builder()
                .result(authService.getMyProfile(id))
                .build();
    }
}
