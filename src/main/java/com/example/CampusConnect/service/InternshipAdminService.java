package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.CreateInternshipDTO;
import com.example.CampusConnect.dto.InternshipDTO;

public interface InternshipAdminService {
    InternshipDTO createInternship(CreateInternshipDTO dto);
    InternshipDTO updateInternship(Long id, CreateInternshipDTO dto);
    void deleteInternship(Long id);
}

