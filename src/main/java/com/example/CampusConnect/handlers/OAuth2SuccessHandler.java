package com.example.CampusConnect.handlers;

import com.example.CampusConnect.model.User;
import com.example.CampusConnect.security.CustomUserDetailsService;
import com.example.CampusConnect.security.jwt.JwtCookieUtil;
import com.example.CampusConnect.security.jwt.JwtService;
import com.example.CampusConnect.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final JwtCookieUtil jwtCookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        DefaultOAuth2User oauthUser =
                (DefaultOAuth2User) oauthToken.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String provider =
                oauthToken.getAuthorizedClientRegistrationId().toUpperCase();

        if (email == null || email.isBlank()) {
            log.error("OAuth2 login failed: Email missing");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Email not found");
            return;
        }

        log.info("OAuth2 login success for email: {}", email);

        User user =
                userService.findOrCreateOAuthUser(email, name, provider);

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(user.getEmail());

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                jwtService.generateRefreshToken(userDetails);

        // Set cookies via centralized util
        jwtCookieUtil.addAccessTokenCookie(response, accessToken);
        jwtCookieUtil.addRefreshTokenCookie(response, refreshToken);

        // Clean redirect
        getRedirectStrategy().sendRedirect(request,
                response,
                "http://localhost:8080/oauth-success.html");

        clearAuthenticationAttributes(request);
    }
}