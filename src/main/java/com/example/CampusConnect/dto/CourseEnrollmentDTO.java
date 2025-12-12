package com.example.CampusConnect.dto;


import lombok.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseEnrollmentDTO {
    private Long id;
    private Long userId;
    private Long courseId;
    private LocalDate enrolledAt;
    private String status;
}
