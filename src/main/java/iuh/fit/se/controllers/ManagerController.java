package iuh.fit.se.controllers;


import iuh.fit.se.dtos.request.ManagerCreationRequest;
import iuh.fit.se.dtos.request.ManagerDeleteRequest;
import iuh.fit.se.dtos.request.ManagerFindRequest;
import iuh.fit.se.dtos.request.ManagerUpdateRequest;
import iuh.fit.se.dtos.response.*;
import iuh.fit.se.entities.User;
import iuh.fit.se.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/managers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ManagerController {
    private final UserService userService;

    @PostMapping
    public ApiResponse<ManagerCreationResponse> createManager(@Valid @RequestBody ManagerCreationRequest request) {

        return ApiResponse.<ManagerCreationResponse>builder()
                .result(userService.createManager(request))
                .build();
    }

    @PostMapping("/delete/{id}")
    public ApiResponse<ManagerDeleteResponse> deleteManager(
            @PathVariable Long id) {

        ManagerDeleteRequest request = new ManagerDeleteRequest();
        request.setId(id);

        return ApiResponse.<ManagerDeleteResponse>builder()
                .result(userService.deleteManager(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ManagerUpdateResponse> updateManager(
            @PathVariable Long id,
            @Valid @RequestBody ManagerUpdateRequest request) {

        return ApiResponse.<ManagerUpdateResponse>builder()
                .result(userService.updateManager(id, request))
                .build();
    }


    @GetMapping("/search")
    public ApiResponse<Page<User>> searchUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ApiResponse.<Page<User>>builder()
                .result(userService.searchUsers(keyword, page))
                .build();
    }

    @GetMapping("/find/{id}")
    public ApiResponse<ManagerFindResponse> findManager(@PathVariable Long id) {

        ManagerFindRequest request = new ManagerFindRequest();
        request.setId(id);

        return ApiResponse.<ManagerFindResponse>builder()
                .result(userService.findManager(request))
                .build();
    }


    @GetMapping("/page")
    public ApiResponse<ManagerPaginationResponse> getManagers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int pageSize,
            @RequestParam(defaultValue = "") String keyWord // <== Thêm tham số keyword
    ) {
        // Gọi Service với tham số keyword mới
        return ApiResponse.<ManagerPaginationResponse>builder()
                .result(userService.getManagers(page, pageSize, keyWord))
                .build();
    }

}
