package iuh.fit.se.controllers;


import iuh.fit.se.dtos.request.CartItemCreationRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.CartItemCreationResponse;
import iuh.fit.se.entities.CartItem;
import iuh.fit.se.services.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart-items")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping
    public ApiResponse<CartItemCreationResponse> createCartItem(@RequestBody CartItemCreationRequest request) {
        return ApiResponse.<CartItemCreationResponse>builder()
                .result(cartItemService.createCartItem(request))
                .build();
    }

}
