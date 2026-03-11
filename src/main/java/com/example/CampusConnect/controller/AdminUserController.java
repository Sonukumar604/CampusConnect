package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.UserDTO;
import com.example.CampusConnect.service.AdminUserService;
import com.example.CampusConnect.security.annotation.CanViewUsers;
import com.example.CampusConnect.security.annotation.CanManageUsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private static final Logger log =
            LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private AdminUserService adminUserService;

    // ================= VIEW USERS =================

    @CanViewUsers
    @GetMapping("/paged")
    public ResponseEntity<Page<UserDTO>> getAllUsersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Admin request: get users paged");
        log.debug("Paging params page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        Page<UserDTO> users =
                adminUserService.getAllUsersPaged(page, size, sortBy, sortDir);

        log.info("Users page fetched successfully");

        return ResponseEntity.ok(users);
    }

    @CanViewUsers
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        log.info("Admin request: get all users");

        return ResponseEntity.ok(
                adminUserService.getAllUsers()
        );
    }

    @CanViewUsers
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {

        log.info("Admin request: get user by id {}", userId);

        return ResponseEntity.ok(
                adminUserService.getUserById(userId)
        );
    }

    // ================= MANAGE USERS =================

    @CanManageUsers
    @PutMapping("/{userId}/role")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {

        log.info("Admin request: update user role");
        log.debug("UserId={}, role={}", userId, role);

        return ResponseEntity.ok(
                adminUserService.updateUserRole(userId, role)
        );
    }

    @CanManageUsers
    @PutMapping("/{id}/block")
    public ResponseEntity<UserDTO> blockUser(@PathVariable Long id) {

        log.warn("Admin request: block user {}", id);

        return ResponseEntity.ok(
                adminUserService.blockUser(id)
        );
    }

    @CanManageUsers
    @PutMapping("/{id}/unblock")
    public ResponseEntity<UserDTO> unblockUser(@PathVariable Long id) {

        log.info("Admin request: unblock user {}", id);

        return ResponseEntity.ok(
                adminUserService.unblockUser(id)
        );
    }

    @CanManageUsers
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {

        log.warn("Admin request: delete user {}", userId);

        adminUserService.deleteUser(userId);

        return ResponseEntity.ok(
                "User deleted successfully with ID: " + userId
        );
    }
}