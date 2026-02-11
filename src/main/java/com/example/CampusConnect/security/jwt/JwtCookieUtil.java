
package com.example.CampusConnect.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieUtil {

    private static final String COOKIE_NAME = "CC_AUTH_TOKEN";
    private static final int COOKIE_EXPIRY = 24 * 60 * 60; // 1 day

    // ✅ Add JWT to HttpOnly cookie
    public void addJwtCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie(COOKIE_NAME, jwt);
        cookie.setHttpOnly(true);       // 🔒 JS cannot access
        cookie.setSecure(false);        // true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_EXPIRY);

        response.addCookie(cookie);
    }

    // ✅ Clear cookie on logout
    public void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
