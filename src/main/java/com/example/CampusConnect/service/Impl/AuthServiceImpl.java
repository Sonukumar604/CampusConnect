package com.example.CampusConnect.service.Impl;
import com.example.CampusConnect.model.Role;

import com.example.CampusConnect.dto.LoginRequestDTO;
import com.example.CampusConnect.dto.LoginResponseDTO;
import com.example.CampusConnect.dto.SignupRequestDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.RefreshToken;
import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.security.CustomUserDetails;
import com.example.CampusConnect.security.CustomUserDetailsService;
import com.example.CampusConnect.security.jwt.JwtCookieUtil;
import com.example.CampusConnect.security.jwt.JwtService;
import com.example.CampusConnect.service.AuthService;
import com.example.CampusConnect.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtCookieUtil jwtCookieUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    // ========================
    // SIGNUP
    // ========================
    @Override
    @Transactional
    public void signup(SignupRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .status(User.Status.ACTIVE)
                .build();

        userRepository.save(user);
    }

    // ========================
    // LOGIN
    // ========================
    @Override
    @Transactional
    public LoginResponseDTO login(@NotNull LoginRequestDTO dto,
                                  HttpServletResponse response) {

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        // 🚨 Prevent blocked users from logging in
        if (user.getStatus() == User.Status.BLOCKED) {
            throw new IllegalStateException("User account is blocked");
        }

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.createRefreshToken(
                user,
                refreshToken,
                jwtService.getRefreshExpiration()
        );

        jwtCookieUtil.addRefreshTokenCookie(response, refreshToken);

        return buildLoginResponse(user, accessToken, refreshToken);
    }

    // ========================
    // REFRESH TOKEN (ROTATION)
    // ========================
    @Override
    @Transactional
    public LoginResponseDTO refreshToken(HttpServletRequest request,
                                         HttpServletResponse response) {

        String refreshTokenValue = extractRefreshToken(request);

        if (refreshTokenValue == null) {
            throw new IllegalArgumentException("Refresh token not found");
        }

        // 1️⃣ Extract username
        String username = jwtService.extractUsername(refreshTokenValue);

        CustomUserDetails userDetails =
                (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        // 2️⃣ Validate JWT signature
        if (!jwtService.isRefreshTokenValid(refreshTokenValue, userDetails)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        // 3️⃣ Validate DB token
        RefreshToken dbToken =
                refreshTokenService.verifyRefreshToken(refreshTokenValue);

        User user = dbToken.getUser();

        if (user.getStatus() == User.Status.BLOCKED) {
            throw new IllegalStateException("User account is blocked");
        }

        // 4️⃣ Revoke old token
        refreshTokenService.revokeToken(refreshTokenValue);

        // 5️⃣ Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.createRefreshToken(
                user,
                newRefreshToken,
                jwtService.getRefreshExpiration()
        );

        jwtCookieUtil.addRefreshTokenCookie(response, newRefreshToken);

        return buildLoginResponse(user, newAccessToken, newRefreshToken);
    }

    // ========================
    // LOGOUT
    // ========================
    @Override
    @Transactional
    public void logout(HttpServletRequest request,
                       HttpServletResponse response) {

        String refreshToken = extractRefreshToken(request);

        if (refreshToken != null) {
            refreshTokenService.revokeToken(refreshToken);
        }

        jwtCookieUtil.clearRefreshTokenCookie(response);
    }

    // ========================
    // HELPER
    // ========================
    private String extractRefreshToken(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("CC_REFRESH_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private LoginResponseDTO buildLoginResponse(User user,
                                                String accessToken,
                                                String refreshToken) {

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
