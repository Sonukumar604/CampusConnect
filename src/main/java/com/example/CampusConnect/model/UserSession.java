package com.example.CampusConnect.model;
import com.example.CampusConnect.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    private String deviceId;

    private String deviceName;

    private String ipAddress;

    private String location;

    private Instant createdAt;

    private Instant lastActiveAt;

    private Instant expiryDate;

    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}