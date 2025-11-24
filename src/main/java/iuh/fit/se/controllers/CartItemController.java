package iuh.fit.se.controllers;


import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.CartItemCreationResponse;
import iuh.fit.se.dtos.response.CartItemDeletionResponse;
import iuh.fit.se.services.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart-items")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping
    public ApiResponse<CartItemCreationResponse> createCartItem(
            @RequestBody CartItemCreationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long customerId = Long.valueOf(jwt.getSubject());
        return ApiResponse.<CartItemCreationResponse>builder()
                .result(cartItemService.createCartItem(request, customerId))
                .build();
    }


    @DeleteMapping("/{id}")
    public ApiResponse<CartItemDeletionResponse> deleteCartItem(
            @PathVariable("id") Long cartItemId,
            @AuthenticationPrincipal Jwt jwt) {
        Long customerId = Long.valueOf(jwt.getSubject());
        System.out.println(cartItemId);
        return ApiResponse.<CartItemDeletionResponse>builder()
                .result(cartItemService.deleteCartItem(cartItemId, customerId))
                .build();
    }


}
