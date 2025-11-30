package iuh.fit.se.controllers;


import iuh.fit.se.dtos.request.ManagerCreationRequest;
import iuh.fit.se.dtos.request.ManagerDeleteRequest;
import iuh.fit.se.dtos.request.ManagerUpdateRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.ManagerCreationResponse;
import iuh.fit.se.dtos.response.ManagerDeleteResponse;
import iuh.fit.se.dtos.response.ManagerUpdateResponse;
import iuh.fit.se.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/managers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ManagerController {
    private final UserService userService;

    @PostMapping
    public ApiResponse<ManagerCreationResponse> createManager(@RequestBody ManagerCreationRequest request) {

        return ApiResponse.<ManagerCreationResponse>builder()
                .result(userService.createManager(request))
                .build();
    }

    @PostMapping("/delete")
    public ApiResponse<ManagerDeleteResponse> deleteManager(
            @RequestBody ManagerDeleteRequest request) {

        return ApiResponse.<ManagerDeleteResponse>builder()
                .result(userService.deleteManager(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ManagerUpdateResponse> updateManager(
            @PathVariable Long id,
            @RequestBody ManagerUpdateRequest request) {

        return ApiResponse.<ManagerUpdateResponse>builder()
                .result(userService.updateManager(id, request))
                .build();
    }

}
