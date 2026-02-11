
package com.example.CampusConnect.dto;

import com.example.CampusConnect.model.User.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;   // optional (useful for Postman / mobile)
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
