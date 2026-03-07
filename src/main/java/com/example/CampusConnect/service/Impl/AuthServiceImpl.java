package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.LoginRequestDTO;
import com.example.CampusConnect.dto.LoginResponseDTO;
import com.example.CampusConnect.dto.SignupRequestDTO;
import com.example.CampusConnect.exceptions.DuplicateResourceException;
import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.model.UserSession;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.security.CustomUserDetails;
import com.example.CampusConnect.security.CustomUserDetailsService;
import com.example.CampusConnect.security.jwt.JwtCookieUtil;
import com.example.CampusConnect.security.jwt.JwtService;
import com.example.CampusConnect.service.AuthService;
import com.example.CampusConnect.service.SessionService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final JwtCookieUtil jwtCookieUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final SessionService sessionService;

    @Override
    public void signup(SignupRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.STUDENT)
                .status(User.Status.ACTIVE)
                .build();

        userRepository.save(user);
    }

    @Override
    public LoginResponseDTO login(@NotNull LoginRequestDTO dto,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        CustomUserDetails userDetails =
                loadAndValidateUser(dto.getEmail(), dto.getPassword());

        User user = userDetails.getUser();

        validateUserStatus(user);

        String accessToken = jwtService.generateAccessToken(userDetails);

        // Create DB session (refresh token stored here)
        UserSession session = sessionService.createSession(
                user,
                generateDeviceId(),
                request.getHeader("User-Agent"),
                request.getRemoteAddr()
        );

        String refreshToken = session.getRefreshToken();

        setAuthCookies(response, accessToken, refreshToken);

        return buildLoginResponse(user, accessToken, refreshToken);
    }

    @Override
    public LoginResponseDTO refreshToken(HttpServletRequest request,
                                         HttpServletResponse response) {

        String refreshToken = extractRefreshToken(request);

        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token not found");
        }

        // Validate session
        UserSession session = sessionService.validateRefreshToken(refreshToken);
        User user = session.getUser();

        validateUserStatus(user);

        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailsService
                        .loadUserByUsername(user.getEmail());

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        sessionService.updateLastActive(session);

        setAuthCookies(response, newAccessToken, refreshToken);

        return buildLoginResponse(user, newAccessToken, refreshToken);
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response) {

        String refreshToken = extractRefreshToken(request);

        if (refreshToken != null) {
            sessionService.findActiveSessionByRefreshToken(refreshToken)
                    .ifPresent(session ->
                            sessionService.deactivateSession(
                                    session.getUser(),
                                    session.getDeviceId()
                            ));
        }

        clearAuthCookies(response);
    }

    private CustomUserDetails loadAndValidateUser(String email, String password) {

        CustomUserDetails userDetails;

        try {
            userDetails = (CustomUserDetails)
                    customUserDetailsService.loadUserByUsername(email);
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!passwordEncoder.matches(password, userDetails.getUser().getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return userDetails;
    }

    private void validateUserStatus(User user) {
        if (user.getStatus() == User.Status.BLOCKED) {
            throw new IllegalStateException("User account is blocked");
        }
    }

    private void setAuthCookies(HttpServletResponse response,
                                String accessToken,
                                String refreshToken) {

        jwtCookieUtil.addAccessTokenCookie(response, accessToken);
        jwtCookieUtil.addRefreshTokenCookie(response, refreshToken);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        jwtCookieUtil.clearAccessTokenCookie(response);
        jwtCookieUtil.clearRefreshTokenCookie(response);
    }

    private String extractRefreshToken(HttpServletRequest request) {

        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("CC_REFRESH_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private String generateDeviceId() {
        return String.valueOf(Instant.now().toEpochMilli());
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