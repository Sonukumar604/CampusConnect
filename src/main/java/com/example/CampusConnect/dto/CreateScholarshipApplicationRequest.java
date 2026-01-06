package com.example.CampusConnect.dto;

import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateScholarshipApplicationRequest {

    @NotNull(message = "Scholarship ID is required")
    private Long scholarshipId;

    @NotBlank(message = "Statement of purpose cannot be blank")
    @Size(min = 20, max = 500, message = "Statement of purpose must be between 20 and 500 characters")
    private String statementOfPurpose;

    @DecimalMin(value = "0.0", message = "GPA cannot be negative")
    @DecimalMax(value = "10.0", message = "GPA cannot exceed 10")
    private Double gpa;
}
