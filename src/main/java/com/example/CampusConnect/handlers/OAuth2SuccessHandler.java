package com.example.CampusConnect.handlers;

import com.example.CampusConnect.security.CustomUserDetailsService;
import com.example.CampusConnect.security.jwt.JwtCookieUtil;
import com.example.CampusConnect.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        DefaultOAuth2User oauthUser =
                (DefaultOAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(email);

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                jwtService.generateRefreshToken(userDetails);

        jwtCookieUtil.addAccessTokenCookie(response, accessToken);
        jwtCookieUtil.addRefreshTokenCookie(response, refreshToken);

        getRedirectStrategy().sendRedirect(
                request,
                response,
                "http://localhost:8080/oauth-success.html"
        );
    }
}