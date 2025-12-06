package iuh.fit.se.controllers;

import iuh.fit.se.dtos.request.CategoryCreationRequest;
import iuh.fit.se.dtos.request.RoleDeleteRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.CategoryCreationResponse;
import iuh.fit.se.dtos.response.RoleDeleteResponse;
import iuh.fit.se.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryCreationResponse> addCategory(@RequestBody CategoryCreationRequest request) {
        return ApiResponse.<CategoryCreationResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }




}
