package iuh.fit.se.controllers;

import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.request.CustomerRegistrationRequest;
import iuh.fit.se.dtos.response.AddCartItemResponse;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.CustomerRegistrationResponse;
import iuh.fit.se.services.AuthService;
import iuh.fit.se.services.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddCartItemResponse>> addItem(
            @Valid @RequestBody CartItemCreationRequest request,
            @AuthenticationPrincipal Jwt jwt,
            @CookieValue(name = "cart", required = false) String cartId) {
        Long userId = null;

        if (jwt != null)
            userId = Long.valueOf(jwt.getSubject());

        Long cartIdValue = cartId == null ? null : Long.valueOf(cartId);

        AddCartItemResponse response = cartService.addItem(request, userId, cartIdValue);

        if (response.getCartId() != null) {
            ResponseCookie cartCookie = ResponseCookie.from("cart", response.getCartId().toString())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cartCookie.toString())
                    .body(ApiResponse.<AddCartItemResponse>builder()
                            .result(response)
                            .message("Thêm vào giỏ hàng thành công cho khách hàng vãng lai")
                            .build());
        }

        // Xoá cart nếu người dùng đã đăng nhập
        ResponseCookie deleteCart = ResponseCookie.from("cart", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCart.toString())
                .body(ApiResponse.<AddCartItemResponse>builder()
                        .result(response)
                        .message("Thêm vào giỏ hàng thành công cho khách hàng đăng nhập")
                        .build());
    }
}
