package com.example.CampusConnect.dto;

import com.example.CampusConnect.model.RegistrationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationDTO {
    private Long id;
    private Long eventId;
    private Long userId;
    private LocalDateTime registrationTime;
    private String notes;
    private RegistrationStatus status;
}

