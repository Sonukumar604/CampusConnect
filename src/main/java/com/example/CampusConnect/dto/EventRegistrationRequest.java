package com.example.CampusConnect.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationRequest {
    @Size(max = 1000)
    private String notes;
}
