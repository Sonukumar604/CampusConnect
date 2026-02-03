package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CreateInternshipDTO;
import com.example.CampusConnect.dto.InternshipDTO;
import com.example.CampusConnect.model.Internship;
import com.example.CampusConnect.repository.InternshipRepository;
import com.example.CampusConnect.service.InternshipAdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InternshipAdminServiceImpl implements InternshipAdminService {

    private static final Logger log =
            LoggerFactory.getLogger(InternshipAdminServiceImpl.class);

    private final InternshipRepository internshipRepository;

    @Override
    public InternshipDTO createInternship(CreateInternshipDTO dto) {

        log.info("Creating internship");

        Internship internship = new Internship();
        BeanUtils.copyProperties(dto, internship);
        internship.setPostedOn(LocalDate.now());

        Internship saved = internshipRepository.save(internship);

        log.info("Internship created successfully with id={}", saved.getId());

        return mapToDto(saved);
    }

    @Override
    public InternshipDTO updateInternship(Long id, CreateInternshipDTO dto) {

        log.info("Updating internship with id={}", id);

        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Internship not found with id={}", id);
                    return new RuntimeException("Internship not found");
                });

        BeanUtils.copyProperties(dto, internship, "id", "postedOn");

        Internship updated = internshipRepository.save(internship);

        log.info("Internship updated successfully with id={}", updated.getId());

        return mapToDto(updated);
    }

    @Override
    public void deleteInternship(Long id) {

        log.info("Deleting internship with id={}", id);

        internshipRepository.deleteById(id);

        log.info("Internship deleted successfully with id={}", id);
    }

    private InternshipDTO mapToDto(Internship internship) {
        InternshipDTO dto = new InternshipDTO();
        BeanUtils.copyProperties(internship, dto);
        return dto;
    }
}
