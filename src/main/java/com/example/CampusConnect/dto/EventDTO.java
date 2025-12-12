package com.example.CampusConnect.dto;

import com.example.CampusConnect.model.EventMode;
import com.example.CampusConnect.model.EventType;
import com.example.CampusConnect.model.PublishStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Long id;
    private String title;
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
