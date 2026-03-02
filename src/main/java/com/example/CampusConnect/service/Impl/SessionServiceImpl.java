package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.model.User;
import com.example.CampusConnect.model.UserSession;
import com.example.CampusConnect.repository.UserSessionRepository;
import com.example.CampusConnect.service.SessionService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {

    private final UserSessionRepository sessionRepository;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenDurationMs;

    private static final int MAX_SESSIONS = 3;

    // =====================================
    // CREATE SESSION (LOGIN)
    // =====================================
    @Override
    public UserSession createSession(User user,
                                     String deviceId,
                                     String deviceName,
                                     String ipAddress) {

        List<UserSession> activeSessions =
                sessionRepository.findByUserAndActiveTrue(user);

        if (activeSessions.size() >= MAX_SESSIONS) {
            deactivateOldestSession(activeSessions);
        }

        UserSession session = UserSession.builder()
                .user(user)
                .deviceId(deviceId)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .refreshToken(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .lastActiveAt(Instant.now())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .active(true)
                .build();

        return sessionRepository.save(session);
    }

    // =====================================
    // VALIDATE REFRESH TOKEN
    // =====================================
    @Override
    public UserSession validateRefreshToken(String refreshToken) {

        UserSession session = sessionRepository
                .findByRefreshTokenAndActiveTrue(refreshToken)
                .orElseThrow(() ->
                        new RuntimeException("Invalid or inactive refresh token"));

        if (session.getExpiryDate() == null ||
                session.getExpiryDate().isBefore(Instant.now())) {

            session.setActive(false);
            sessionRepository.save(session);

            throw new RuntimeException("Refresh token expired");
        }

        return session;
    }

    // =====================================
    // FIND ACTIVE SESSION
    // =====================================
    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> findActiveSessionByRefreshToken(String refreshToken) {
        return sessionRepository.findByRefreshTokenAndActiveTrue(refreshToken);
    }

    // =====================================
    // DEACTIVATE SPECIFIC DEVICE
    // =====================================
    @Override
    public void deactivateSession(User user, String deviceId) {

        UserSession session = sessionRepository
                .findByUserAndDeviceIdAndActiveTrue(user, deviceId)
                .orElseThrow(() ->
                        new RuntimeException("Active session not found"));

        session.setActive(false);
        sessionRepository.save(session);
    }

    // =====================================
    // DEACTIVATE ALL SESSIONS
    // =====================================
    @Override
    public void deactivateAllSessions(User user) {

        List<UserSession> activeSessions =
                sessionRepository.findByUserAndActiveTrue(user);

        for (UserSession session : activeSessions) {
            session.setActive(false);
            sessionRepository.save(session);
        }
    }

    // =====================================
    // UPDATE LAST ACTIVE
    // =====================================
    @Override
    public void updateLastActive(UserSession session) {
        session.setLastActiveAt(Instant.now());
        sessionRepository.save(session);
    }

    // =====================================
    // GET ACTIVE SESSIONS
    // =====================================
    @Override
    @Transactional(readOnly = true)
    public List<UserSession> getActiveSessions(User user) {
        return sessionRepository.findByUserAndActiveTrue(user);
    }

    // =====================================
    // PRIVATE HELPER
    // =====================================
    private void deactivateOldestSession(List<UserSession> sessions) {

        sessions.sort(Comparator.comparing(UserSession::getCreatedAt));

        UserSession oldest = sessions.get(0);
        oldest.setActive(false);

        sessionRepository.save(oldest);
    }
}