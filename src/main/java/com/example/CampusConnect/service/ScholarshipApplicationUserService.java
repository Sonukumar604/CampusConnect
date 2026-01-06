
package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.CreateScholarshipApplicationRequest;
import com.example.CampusConnect.dto.ScholarshipApplicationResponseDTO;

import java.util.List;

public interface ScholarshipApplicationUserService {

    ScholarshipApplicationResponseDTO apply(Long userId, CreateScholarshipApplicationRequest request);

    List<ScholarshipApplicationResponseDTO> getMyApplications(Long userId);
}
