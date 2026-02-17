package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.LoginRequestDTO;
import com.example.CampusConnect.dto.LoginResponseDTO;
import com.example.CampusConnect.dto.SignupRequestDTO;
import com.example.CampusConnect.model.RefreshToken;
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
    public void signup(@NotNull SignupRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .status(User.Status.ACTIVE)
                .build();

        userRepository.save(user);
    }

    // ========================
    // LOGIN
    // ========================
    @Override
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
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        // Generate Tokens
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Store Refresh Token in DB
        refreshTokenService.createRefreshToken(
                user,
                refreshToken,
                jwtService.getRefreshExpiration()
        );

        // Add HttpOnly Cookie
        jwtCookieUtil.addRefreshTokenCookie(response, refreshToken);

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

    // ========================
    // REFRESH TOKEN
    // ========================
    @Override
    public LoginResponseDTO refreshToken(HttpServletRequest request,
                                         HttpServletResponse response) {

        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token not found");
        }

        //  Step 1: Validate token exists in DB + not revoked + not expired
        RefreshToken dbToken = refreshTokenService.verifyRefreshToken(refreshToken);

        String username = jwtService.extractUsername(refreshToken);

        CustomUserDetails userDetails =
                (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        // Cryptographic validation
        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        User user = dbToken.getUser();

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        // Store new refresh token in DB
        refreshTokenService.createRefreshToken(
                user,
                newRefreshToken,
                jwtService.getRefreshExpiration()
        );

        // Replace cookie
        jwtCookieUtil.addRefreshTokenCookie(response, newRefreshToken);

        return LoginResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    // ========================
    // LOGOUT
    // ========================
    @Override
    public void logout(HttpServletResponse response) {
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
}
