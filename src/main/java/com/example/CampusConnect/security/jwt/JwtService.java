package com.example.CampusConnect.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;


    public long getAccessExpiration() {
        return accessTokenExpiration;
    }
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // ==========================
    // ACCESS TOKEN
    // ==========================
    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, accessTokenExpiration, "ACCESS");
    }

    // ==========================
    // REFRESH TOKEN
    // ==========================
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshExpiration, "REFRESH");

    }

    // Common builder
    private String buildToken(UserDetails userDetails,
                              long expiration,
                              String tokenType) {

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .claim("tokenType", tokenType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ==========================
    // Extraction Methods
    // ==========================
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractTokenType(String token) {
        return extractAllClaims(token).get("tokenType", String.class);
    }

    // ==========================
    // Validation
    // ==========================
    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, "ACCESS");
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, "REFRESH");
    }

    private boolean isTokenValid(String token,
                                 UserDetails userDetails,
                                 String expectedType) {
        try {
            final String username = extractUsername(token);
            final String tokenType = extractTokenType(token);

            return username.equals(userDetails.getUsername())
                    && tokenType.equals(expectedType)
                    && !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Backward compatibility
    public String generateToken(UserDetails userDetails) {
        return generateAccessToken(userDetails);
    }
    public long getRefreshExpiration() {
        return refreshExpiration;
    }
}
