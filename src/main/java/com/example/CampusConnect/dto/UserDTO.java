package com.example.CampusConnect.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
}
