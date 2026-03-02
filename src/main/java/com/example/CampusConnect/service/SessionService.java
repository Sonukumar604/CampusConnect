package com.example.CampusConnect.service;

import com.example.CampusConnect.model.User;
import com.example.CampusConnect.model.UserSession;

import java.util.List;
import java.util.Optional;

public interface SessionService {

    /**
     * Create a new session on login
     */
    UserSession createSession(User user,
                              String deviceId,
                              String deviceName,
                              String ipAddress);

    /**
     * Validate refresh token and return active session
     */
    UserSession validateRefreshToken(String refreshToken);

    /**
     * Find active session by refresh token (for business use)
     */
    Optional<UserSession> findActiveSessionByRefreshToken(String refreshToken);

    /**
     * Deactivate a specific device session
     */
    void deactivateSession(User user, String deviceId);

    /**
     * Deactivate all sessions of a user (logout everywhere)
     */
    void deactivateAllSessions(User user);

    /**
     * Update last active timestamp
     */
    void updateLastActive(UserSession session);

    /**
     * Get all active sessions of a user
     */
    List<UserSession> getActiveSessions(User user);
}