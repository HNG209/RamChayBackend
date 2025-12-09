package iuh.fit.se.controllers;


import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.request.CartItemUpdateRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.CartItemCreationResponse;
import iuh.fit.se.dtos.response.CartItemDeletionResponse;
import iuh.fit.se.dtos.response.GetItemsResponse;
import iuh.fit.se.entities.CartItem;
import iuh.fit.se.services.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
        Long userId = null;

        if (jwt != null)
            userId = Long.valueOf(jwt.getSubject());

        return ApiResponse.<CartItemDeletionResponse>builder()
                .result(cartItemService.deleteCartItem(cartItemId, userId))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<GetItemsResponse>> getCartItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal Jwt jwt,
            @CookieValue(name = "cart", required = false) String cartId) {
        Long userId = null;

        if (jwt != null)
            userId = Long.valueOf(jwt.getSubject());

        return ApiResponse.<Page<GetItemsResponse>>builder()
                .result(cartItemService.getItemsByCartIdOrCustomerId(
                        cartId == null ? null : Long.valueOf(cartId),
                        userId,
                        page,
                        size))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<GetItemsResponse> updateCartItem(
            @PathVariable("id") Long cartItemId,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CartItemUpdateRequest request) {
        Long userId = null;

        if (jwt != null)
            userId = Long.valueOf(jwt.getSubject());

        return ApiResponse.<GetItemsResponse>builder()
                .result(cartItemService.updateCartItem(cartItemId, userId, request))
                .build();
    }
}
