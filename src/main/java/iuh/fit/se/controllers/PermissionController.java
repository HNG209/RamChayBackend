package iuh.fit.se.controllers;

import iuh.fit.se.entities.Permission;
import iuh.fit.se.services.impl.PermissionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PermissionController {
    private  final PermissionServiceImpl  permissionService;

    @GetMapping
    public ResponseEntity<List<Permission>> findAll() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }


}
