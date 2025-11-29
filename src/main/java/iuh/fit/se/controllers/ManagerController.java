package iuh.fit.se.controllers;


import iuh.fit.se.dtos.request.ManagerCreationRequest;
import iuh.fit.se.dtos.response.ApiResponse;
import iuh.fit.se.dtos.response.ManagerCreationResponse;
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
}
