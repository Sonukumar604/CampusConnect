package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.UserDTO;
import com.example.CampusConnect.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping("/paged")
    public ResponseEntity<Page<UserDTO>> getAllUsersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<UserDTO> users = adminUserService.getAllUsersPaged(page, size, sortBy, sortDir);
        return ResponseEntity.ok(users);
    }

    // ✅ Get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // ✅ Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.getUserById(userId));
    }

    // ✅ Update user role
    @PutMapping("/{userId}/role")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role
    ) {
        return ResponseEntity.ok(adminUserService.updateUserRole(userId, role));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<UserDTO> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.blockUser(id));
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<UserDTO> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.unblockUser(id));
    }

    // ✅ Delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully with ID: " + userId);
    }
}
