package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.User;
import com.example.CampusConnect.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    // Used in refresh validation
    Optional<UserSession> findByRefreshTokenAndActiveTrue(String refreshToken);

    // Used for device logout
    Optional<UserSession> findByUserAndDeviceIdAndActiveTrue(User user, String deviceId);

    // Used for session dashboard + login limit
    List<UserSession> findByUserAndActiveTrue(User user);
}
