package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.InternshipDTO;
import com.example.CampusConnect.model.InternshipType;
import org.springframework.data.domain.Page;

public interface InternshipUserService {
    Page<InternshipDTO> getAllInternships(int page, int size, String sortBy);
    Page<InternshipDTO> filterByLocation(String location, int page, int size);
    Page<InternshipDTO> filterByType(InternshipType type, int page, int size);
    Page<InternshipDTO> search(String keyword, int page, int size);
}

