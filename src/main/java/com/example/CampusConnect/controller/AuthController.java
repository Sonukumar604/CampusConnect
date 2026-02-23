package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.LoginRequestDTO;
import com.example.CampusConnect.dto.LoginResponseDTO;
import com.example.CampusConnect.dto.SignupRequestDTO;
import com.example.CampusConnect.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    // ==============================
    // SIGNUP
    // ==============================
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Valid @RequestBody SignupRequestDTO signupRequestDTO
    ) {
        authService.signup(signupRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    // ==============================
    // ✅ LOGIN
    // ==============================
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO dto,
            HttpServletResponse response) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        LoginResponseDTO result = authService.login(dto, response);

        return ResponseEntity.ok(result);
    }

    // ==============================
    // REFRESH TOKEN
    // ==============================
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        LoginResponseDTO loginResponse =
                authService.refreshToken(request, response);

        return ResponseEntity.ok(loginResponse);
    }

    // ==============================
    // LOGOUT
    // ==============================
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        authService.logout(request, response);

        return ResponseEntity.ok("Logged out successfully");
    }

}
