package com.example.CampusConnect.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseEnrollmentRequest {
    @NotNull(message = "some data may be required on UI - leave optional")
    private String extra;
    // placeholder if needed; you can remove
    private String note;
}
