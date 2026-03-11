package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.LoginRequestDTO;
import com.example.CampusConnect.dto.LoginResponseDTO;
import com.example.CampusConnect.dto.SignupRequestDTO;
import com.example.CampusConnect.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    // ================= SIGNUP =================

    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Valid @RequestBody SignupRequestDTO signupRequestDTO
    ) {

        log.info("Signup request received for email={}", signupRequestDTO.getEmail());

        authService.signup(signupRequestDTO);

        log.info("User registered successfully email={}", signupRequestDTO.getEmail());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        log.info("Login request for email={}", dto.getEmail());

        LoginResponseDTO result =
                authService.login(dto, request, response);

        log.info("Login successful for userId={}", result.getUserId());

        return ResponseEntity.ok(result);
    }

    // ================= REFRESH TOKEN =================

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        log.info("Refresh token request received");

        LoginResponseDTO loginResponse =
                authService.refreshToken(request, response);

        log.info("Access token refreshed for userId={}", loginResponse.getUserId());

        return ResponseEntity.ok(loginResponse);
    }

    // ================= LOGOUT =================

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        log.info("Logout request received");

        authService.logout(request, response);

        log.info("User logged out successfully");

        return ResponseEntity.ok("Logged out successfully");
    }
}