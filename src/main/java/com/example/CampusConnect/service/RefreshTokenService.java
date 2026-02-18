package com.example.CampusConnect.service;

import com.example.CampusConnect.model.RefreshToken;
import com.example.CampusConnect.model.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user, String token, long expirationMillis);

    RefreshToken verifyRefreshToken(String token);

    void revokeToken(String token);
    void deleteByUser(User user);
}
