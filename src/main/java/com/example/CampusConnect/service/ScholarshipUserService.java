package com.example.CampusConnect.service;


import com.example.CampusConnect.dto.*;
import com.example.CampusConnect.util.PagedResponse;

import java.time.LocalDate;

public interface ScholarshipUserService {
    PagedResponse<ScholarshipDTO> listPublishedScholarships(int page, int size, String sortBy, String sortDir);
    PagedResponse<ScholarshipDTO> filterScholarships(int page, int size, String sortBy, String sortDir,
                                                     String category, String provider,
                                                     Double minAmount, Double maxAmount,
                                                     LocalDate deadlineBefore, LocalDate deadlineAfter,
                                                     String eligibilityContains);
    ScholarshipDTO getScholarshipDetails(Long id);
}
