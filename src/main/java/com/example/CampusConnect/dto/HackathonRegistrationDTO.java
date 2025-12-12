package com.example.CampusConnect.dto;
import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HackathonRegistrationDTO {

    private Long id;

    private String teamName;
    private String projectIdea;
    private boolean approved;

    private LocalDateTime registeredAt;
    private LocalDateTime registrationDate;

    private Long userId;
    private Long hackathonId;
}
