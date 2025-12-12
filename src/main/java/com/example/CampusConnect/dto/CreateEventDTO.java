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
public class CreateEventDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000)
    private String description;

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @NotNull(message = "Mode is required")
    private EventMode mode;

    private String location; // optional for online

    private String speakerName;

    private String hostOrganization;

    @Min(1)
    private Integer registrationLimit;

    @NotNull(message = "Start date/time is required")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date/time is required")
    private LocalDateTime endDateTime;

    private PublishStatus publishStatus = PublishStatus.DRAFT;
}
