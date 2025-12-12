package com.example.CampusConnect.dto;

import com.example.CampusConnect.model.InternshipType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInternshipDTO {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Internship type must be specified (REMOTE, HYBRID, or ONSITE)")
    private InternshipType type;

    @NotBlank(message = "Stipend information is required")
    private String stipend;

    @NotBlank(message = "Duration is required")
    private String duration;

    @NotBlank(message = "Skills required cannot be empty")
    private String skillsRequired;

    @NotBlank(message = "Apply link is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "Apply link must be a valid URL")
    private String applyLink;

    @NotNull(message = "Last date to apply is required")
    @FutureOrPresent(message = "Last date cannot be in the past")
    private LocalDate lastDateToApply;
}
