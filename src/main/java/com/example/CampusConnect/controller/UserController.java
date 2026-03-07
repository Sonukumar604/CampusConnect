package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.*;
import com.example.CampusConnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
public class UserController {

    private static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // ✅ Get current authenticated user
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(
            org.springframework.security.core.Authentication authentication) {

        log.info("Request: fetch current authenticated user");

        String email = authentication.getName();

        UserDTO user = userService.getUserByEmail(email);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Current user fetched successfully", user)
        );
    }



    // ✅ Register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(
            @RequestBody CreateUserDTO createUserDTO) {

        log.info("User request: register new user");
        log.debug("email={}", createUserDTO.getEmail());

        UserDTO createdUser = userService.registerUser(createUserDTO);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "User registered successfully", createdUser)
        );
    }


    // ✅ Get all users
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        log.info("Request: fetch all users");

        List<UserDTO> users = userService.getAllUsers();

        log.debug("Total users fetched={}", users.size());

        return ResponseEntity.ok(
                new ApiResponse<>(200, "All users fetched successfully", users)
        );
    }

    // ✅ Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        log.info("Request: fetch user by id");
        log.debug("userId={}", id);

        UserDTO user = userService.getUserById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "User fetched successfully", user)
        );
    }

    // ✅ Update user
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserDTO dto) {

        log.info("User request: update profile");
        log.debug("userId={}", id);

        UserDTO updated = userService.updateUser(id, dto);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "User updated successfully", updated)
        );
    }

    // ✅ Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        log.warn("Request: delete user");
        log.debug("userId={}", id);

        userService.deleteUser(id);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "User deleted successfully", "Deleted ID: " + id)
        );
    }
}
