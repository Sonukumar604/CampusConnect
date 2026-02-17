
package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.exceptions.TokenRefreshException;
import com.example.CampusConnect.model.RefreshToken;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.RefreshTokenRepository;
import com.example.CampusConnect.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken createRefreshToken(User user, String token, long expirationMillis) {

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plusMillis(expirationMillis))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found in database"));

        if (refreshToken.isRevoked()) {
            throw new TokenRefreshException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenRefreshException("Refresh token expired");
        }

        return refreshToken;
    }

    @Override
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
