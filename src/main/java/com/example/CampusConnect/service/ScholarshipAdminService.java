package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.*;
import com.example.CampusConnect.util.PagedResponse;
import org.springframework.data.domain.Page;

public interface ScholarshipAdminService {
    ScholarshipDTO createScholarship(Long adminId, CreateScholarshipDTO dto);
    ScholarshipDTO updateScholarship(Long adminId, Long scholarshipId, UpdateScholarshipDTO dto);
    void deleteScholarship(Long adminId, Long scholarshipId);
    ScholarshipDTO getScholarshipById(Long scholarshipId);
    PagedResponse<ScholarshipDTO> getScholarshipsPaged(int page, int size, String sortBy, String sortDir, String category);
}
