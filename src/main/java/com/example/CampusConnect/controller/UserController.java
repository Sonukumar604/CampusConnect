package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.*;
import com.example.CampusConnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ Register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@RequestBody CreateUserDTO createUserDTO) {
        UserDTO createdUser = userService.registerUser(createUserDTO);
        return ResponseEntity.ok(new ApiResponse<>(200, "User registered successfully", createdUser));
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDTO>> loginUser(@RequestBody LoginRequest loginRequest) {
        UserDTO loggedUser = userService.loginUser(loginRequest);
        return ResponseEntity.ok(new ApiResponse<>(200, "Login successful", loggedUser));
    }

    // ✅ Get all users
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(200, "All users fetched successfully", users));
    }

    // ✅ Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "User fetched successfully", user));
    }

    // ✅ Update user
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
        UserDTO updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(200, "User updated successfully", updated));
    }

    // ✅ Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "User deleted successfully", "Deleted ID: " + id));
    }
}
