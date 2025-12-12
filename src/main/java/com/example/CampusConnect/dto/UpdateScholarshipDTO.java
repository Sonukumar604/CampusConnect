package com.example.CampusConnect.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateScholarshipDTO {
    @NotBlank
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotNull
    private String category;

    private String eligibilityCriteria;

    @NotNull
    @PositiveOrZero
    private Double amount;

    @NotNull
    @Future
    private LocalDate deadline;

    private String provider;
}
