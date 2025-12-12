package com.example.CampusConnect.service.Impl;


import com.example.CampusConnect.dto.CreateInternshipDTO;
import com.example.CampusConnect.dto.InternshipDTO;
import com.example.CampusConnect.model.Internship;
import com.example.CampusConnect.repository.InternshipRepository;
import com.example.CampusConnect.service.InternshipAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InternshipAdminServiceImpl implements InternshipAdminService {

    private final InternshipRepository internshipRepository;

    @Override
    public InternshipDTO createInternship(CreateInternshipDTO dto) {
        Internship internship = new Internship();
        BeanUtils.copyProperties(dto, internship);
        internship.setPostedOn(LocalDate.now());
        Internship saved = internshipRepository.save(internship);
        return mapToDto(saved);
    }

    @Override
    public InternshipDTO updateInternship(Long id, CreateInternshipDTO dto) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        BeanUtils.copyProperties(dto, internship, "id", "postedOn");
        Internship updated = internshipRepository.save(internship);
        return mapToDto(updated);
    }

    @Override
    public void deleteInternship(Long id) {
        internshipRepository.deleteById(id);
    }

    private InternshipDTO mapToDto(Internship internship) {
        InternshipDTO dto = new InternshipDTO();
        BeanUtils.copyProperties(internship, dto);
        return dto;
    }
}
