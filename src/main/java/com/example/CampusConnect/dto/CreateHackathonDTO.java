package com.example.CampusConnect.dto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHackathonDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Hackathon title must be between 3 and 100 characters")
    private String title;
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;
    @NotBlank(message = "Technology field is required")
    @Size(min = 2, max = 100, message = "Technology must be between 2 and 100 characters")
    private String technology;

    @NotBlank(message = "Organization cannot be blank")
    private String organization;

    @NotBlank(message = "Mode cannot be blank (ONLINE, OFFLINE, HYBRID)")
    private String mode;

    private String location;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    @Future(message = "End date must be a future date")
    private LocalDate endDate;

    @PositiveOrZero(message = "Prize pool must be a positive number or zero")
    private String prizePool;

    @Min(value = 1, message = "There must be at least 1 participant")
    @Max(value = 1000, message = "Max participants cannot exceed 1000")
    private int maxParticipants;
}
