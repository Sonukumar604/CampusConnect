package com.example.CampusConnect.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieUtil {

    private static final String REFRESH_COOKIE_NAME = "CC_REFRESH_TOKEN";

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.same-site}")
    private String sameSite;

    @Value("${cookie.domain:}")
    private String cookieDomain;

    /**
     * 🔐 Add Refresh Token as HttpOnly Cookie
     */
    public void addRefreshTokenCookie(HttpServletResponse response,
                                      String refreshToken) {

        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth/refresh");

        // Convert milliseconds → seconds
        cookie.setMaxAge((int) (refreshTokenExpiration / 1000));

        // Set domain if provided
        if (!cookieDomain.isBlank()) {
            cookie.setDomain(cookieDomain);
        }

        // SameSite attribute (modern browsers)
        cookie.setAttribute("SameSite", sameSite);

        response.addCookie(cookie);
    }

    /**
     * 🗑 Clear Refresh Token Cookie
     */
    public void clearRefreshTokenCookie(HttpServletResponse response) {

        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, null);

        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(0);

        if (!cookieDomain.isBlank()) {
            cookie.setDomain(cookieDomain);
        }

        response.addCookie(cookie);
    }
}
