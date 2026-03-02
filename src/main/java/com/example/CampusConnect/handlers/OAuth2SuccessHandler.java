package com.example.CampusConnect.handlers;

import com.example.CampusConnect.security.CustomUserDetailsService;
import com.example.CampusConnect.security.jwt.JwtCookieUtil;
import com.example.CampusConnect.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final JwtCookieUtil jwtCookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        try {

            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

            String email = oauthUser.getAttribute("email");

            log.info("OAuth2 login success. Email: {}", email);

            if (email == null || email.isBlank()) {
                log.error("OAuth2 email is null or blank");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not found from OAuth provider");
                return;
            }

            // Load user from DB
            UserDetails userDetails =
                    customUserDetailsService.loadUserByUsername(email);

            if (userDetails == null) {
                log.error("UserDetails not found for email: {}", email);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            // Generate JWTs
            String accessToken =
                    jwtService.generateAccessToken(userDetails);

            String refreshToken =
                    jwtService.generateRefreshToken(userDetails);

            // Add cookies
            jwtCookieUtil.addAccessTokenCookie(response, accessToken);
            jwtCookieUtil.addRefreshTokenCookie(response, refreshToken);

            log.info("JWT tokens generated and cookies set successfully");

            // Important: Clear context to avoid session issues
            SecurityContextHolder.clearContext();

            // Redirect to frontend page
            getRedirectStrategy().sendRedirect(
                    request,
                    response,
                    "/oauth-success.html"
            );

        } catch (Exception ex) {
            log.error("OAuth2 success handler failed", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 processing failed");
        }
    }
}