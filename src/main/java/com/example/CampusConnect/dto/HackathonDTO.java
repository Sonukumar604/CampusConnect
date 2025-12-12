package com.example.CampusConnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HackathonDTO {
    private Long id;
    private String title;
    private String technology;
    private String organization;
    private String mode;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal prizePool;
    private boolean active;
    private int maxParticipants;
    private int registeredParticipantsCount;
    private boolean registrationsOpen;

}
