package com.example.CampusConnect.dto;

import com.example.CampusConnect.model.CourseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDTO {
    private Long id;
    private String title;
    private String instructor;
    private String platform;
    private String domain;
    private String technology;
    private String level;
    private Integer durationWeeks;
    private boolean free;
    private Double price;
    private String url;
    private String description;
    private CourseType courseType;
}
