package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.LoginRequestDTO;
import com.example.CampusConnect.dto.LoginResponseDTO;
import com.example.CampusConnect.dto.SignupRequestDTO;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final JwtCookieUtil jwtCookieUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
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

        CustomUserDetails userDetails;

        try {
            userDetails = (CustomUserDetails)
                    customUserDetailsService.loadUserByUsername(dto.getEmail());
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userDetails.getUser();

        // Validate password manually (clean + no circular dependency)
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

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

        String username = jwtService.extractUsername(refreshTokenValue);

        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailsService
                        .loadUserByUsername(username);

        if (!jwtService.isRefreshTokenValid(refreshTokenValue, userDetails)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        RefreshToken dbToken =
                refreshTokenService.verifyRefreshToken(refreshTokenValue);

        User user = dbToken.getUser();

        if (user.getStatus() == User.Status.BLOCKED) {
            throw new IllegalStateException("User account is blocked");
        }

        refreshTokenService.revokeToken(refreshTokenValue);

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
    // HELPERS
    // ========================
    private String extractRefreshToken(HttpServletRequest request) {

        if (request.getCookies() == null) return null;

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