package com.example.CampusConnect.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScholarshipDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String eligibilityCriteria;
    private Double amount;
    private LocalDate deadline;
    private String provider;
    private String publishStatus;
    private Long createdById;
    private String createdByName;
    private Integer applicationCount;
}
