package com.example.CampusConnect.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieUtil {

    private static final String ACCESS_COOKIE_NAME = "CC_ACCESS_TOKEN";
    private static final String REFRESH_COOKIE_NAME = "CC_REFRESH_TOKEN";

    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.same-site}")
    private String sameSite;

    @Value("${cookie.domain:}")
    private String cookieDomain;

    /* ==============================
       Add Access Token Cookie
       ============================== */
    public void addAccessTokenCookie(HttpServletResponse response,
                                     String accessToken) {

        Cookie cookie = new Cookie(ACCESS_COOKIE_NAME, accessToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");

        cookie.setMaxAge((int) (accessTokenExpiration / 1000));

        if (!cookieDomain.isBlank()) {
            cookie.setDomain(cookieDomain);
        }

        cookie.setAttribute("SameSite", sameSite);

        response.addCookie(cookie);
    }

    /* ==============================
       Add Refresh Token Cookie
       ============================== */
    public void addRefreshTokenCookie(HttpServletResponse response,
                                      String refreshToken) {

        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);

        // Only sent to refresh endpoint
        cookie.setPath("/api/auth/refresh");

        cookie.setMaxAge((int) (refreshTokenExpiration / 1000));

        if (!cookieDomain.isBlank()) {
            cookie.setDomain(cookieDomain);
        }

        cookie.setAttribute("SameSite", sameSite);

        response.addCookie(cookie);
    }

    /* ==============================
       Clear Access Cookie
       ============================== */
    public void clearAccessTokenCookie(HttpServletResponse response) {

        Cookie cookie = new Cookie(ACCESS_COOKIE_NAME, null);

        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        if (!cookieDomain.isBlank()) {
            cookie.setDomain(cookieDomain);
        }

        response.addCookie(cookie);
    }

    /* ==============================
       Clear Refresh Cookie
       ============================== */
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