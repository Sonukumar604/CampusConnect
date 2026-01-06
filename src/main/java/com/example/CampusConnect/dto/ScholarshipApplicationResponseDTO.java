
package com.example.CampusConnect.dto;

import lombok.Data;

@Data
public class ScholarshipApplicationResponseDTO {
    private Long id;
    private Long scholarshipId;
    private Long userId;
    private String sop;
    private String status;
}
