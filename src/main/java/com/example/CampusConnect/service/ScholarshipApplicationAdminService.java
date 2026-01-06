
package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.ScholarshipApplicationResponseDTO;

import java.util.List;

public interface ScholarshipApplicationAdminService {

    List<ScholarshipApplicationResponseDTO> getApplicationsForScholarship(Long scholarshipId);

    ScholarshipApplicationResponseDTO updateStatus(Long applicationId, String status);
}
