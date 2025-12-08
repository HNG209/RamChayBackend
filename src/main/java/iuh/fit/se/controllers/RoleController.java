package iuh.fit.se.controllers;

import iuh.fit.se.dtos.request.RoleCreationRequest;
import iuh.fit.se.dtos.request.RoleDeleteRequest;
import iuh.fit.se.dtos.response.*;
import iuh.fit.se.entities.Role;
import iuh.fit.se.services.impl.RoleServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RoleController {
    private final RoleServiceImpl roleServiceImpl;

    @GetMapping
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(roleServiceImpl.getRoles());
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleServiceImpl.getAllRoles());
    }

    @PostMapping
    public ApiResponse<RoleCreationResponse>  createRole(@Valid @RequestBody RoleCreationRequest request){
        return  ApiResponse.<RoleCreationResponse>builder()
                .result(roleServiceImpl.createRole(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleCreationResponse> updateRole(
            @PathVariable Long id,
            @Valid  @RequestBody RoleCreationRequest request){

        return ApiResponse.<RoleCreationResponse>builder()
                .result(roleServiceImpl.updateRole(id,request))
                .build();
    }

    @GetMapping("/find/{id}")
    public ApiResponse<RoleFindResponse> findRoleById(@PathVariable Long id){
        return  ApiResponse.<RoleFindResponse>builder()
                .result(roleServiceImpl.getRoleById(id))
                .build();
    }


    @PostMapping("/delete/{id}")
    public ApiResponse<RoleDeleteResponse> deleteCategory(@PathVariable("id") Long id) {
        RoleDeleteRequest request = new RoleDeleteRequest();
        request.setId(id);

        return ApiResponse.<RoleDeleteResponse>builder()
                .result(roleServiceImpl.deleteRole(request))
                .build();
    }

    @GetMapping("/page")
    public ApiResponse<RolePaginationResponse> getRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize,
            @RequestParam(defaultValue = "") String keyWord // <== Thêm tham số keyword
    ) {
        // Gọi Service với tham số keyword mới
        return ApiResponse.<RolePaginationResponse>builder()
                .result(roleServiceImpl.getRoles(page, pageSize, keyWord))
                .build();
    }



}
