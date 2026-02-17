package com.example.CampusConnect.dto;

import com.example.CampusConnect.model.User.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDTO {

    // 🔐 Short-lived token (used in Authorization header)
    private String accessToken;

    //  Long-lived token (used to generate new access token)
    private String refreshToken;

    // Standard type (Bearer)
    private String tokenType;

    // 👤 User Info (useful for frontend state)
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
