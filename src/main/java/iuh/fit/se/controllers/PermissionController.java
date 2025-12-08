package iuh.fit.se.controllers;

import iuh.fit.se.dtos.request.PermissionCreationRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.PermissionCreationResponse;
import iuh.fit.se.dtos.response.PermissionPaginationResponse;
import iuh.fit.se.entities.Permission;
import iuh.fit.se.services.impl.PermissionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionServiceImpl permissionService;

    @GetMapping
    public ResponseEntity<List<Permission>> findAll() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @PostMapping
    public ApiResponse<PermissionCreationResponse> createPermisison(@Valid @RequestBody PermissionCreationRequest permission) {
        return ApiResponse.<PermissionCreationResponse>
                        builder()
                .result(permissionService.createPermission(permission))
                .build();
    }


    @GetMapping("/page")
    public ApiResponse<PermissionPaginationResponse> getManagers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize,
            @RequestParam(defaultValue = "") String keyWord // <== Thêm tham số keyword
    ) {
        // Gọi Service với tham số keyword mới
        return ApiResponse.<PermissionPaginationResponse>builder()
                .result(permissionService.getPermission(page, pageSize, keyWord))
                .build();
    }


}
