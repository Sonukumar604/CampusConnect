package com.example.CampusConnect.dto;

import com.example.CampusConnect.model.InternshipType;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternshipDTO {

    private Long id;
    private String companyName;
    private String role;
    private String location;
    private InternshipType type;
    private String stipend;
    private String duration;
    private String skillsRequired;
    private String applyLink;
    private LocalDate lastDateToApply;
    private LocalDate postedOn;
}
