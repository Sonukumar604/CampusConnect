package com.example.CampusConnect.dto;

import com.example.CampusConnect.model.EventMode;
import com.example.CampusConnect.model.EventType;
import com.example.CampusConnect.model.PublishStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventDTO {

    @NotNull
    private Long id;

    @NotBlank
    private String title;

    @Size(max = 2000)
    private String description;

    private EventType eventType;

    private EventMode mode;

    private String location;

    private String speakerName;

    private String hostOrganization;

    private Integer registrationLimit;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private PublishStatus publishStatus;
}

