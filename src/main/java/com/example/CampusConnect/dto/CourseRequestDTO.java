package com.example.CampusConnect.dto;

import com.example.CampusConnect.model.CourseType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRequestDTO {
    @NotBlank(message = "Course title is required")
    private String title;

    @NotBlank(message = "Instructor name is required")
    private String instructor;

    @NotBlank(message = "Platform is required")
    private String platform;

    @NotBlank(message = "Domain is required (DSA, Web-Dev, AI-ML, etc.)")
    private String domain;

    @NotBlank(message = "Technology is required")
    private String technology;

    @NotBlank(message = "Level is required (Beginner, Intermediate, Advanced)")
    private String level;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 week")
    private Integer durationWeeks;

    private boolean free;
    private Double price;
    private String url;
    private String description;

    @NotNull(message = "Course type is required")
    private CourseType courseType;
}
