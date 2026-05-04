package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.InternshipDTO;
import com.example.CampusConnect.model.Internship;
import com.example.CampusConnect.model.InternshipType;
import com.example.CampusConnect.repository.InternshipRepository;
import com.example.CampusConnect.service.InternshipUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Transactional(readOnly = true)
public class InternshipUserServiceImpl implements InternshipUserService {

    private static final Logger log =
            LoggerFactory.getLogger(InternshipUserServiceImpl.class);

    private final InternshipRepository internshipRepository;

    @Override
    public Page<InternshipDTO> getAllInternships(int page, int size, String sortBy) {

        log.info("Fetching all internships | page={}, size={}, sortBy={}", page, size, sortBy);

        Pageable pageable = buildPageable(page, size, sortBy);

        return internshipRepository
                .findAll(pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<InternshipDTO> filterByLocation(String location, int page, int size) {

        log.info("Filtering internships by location='{}'", location);

        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size)
        );

        return internshipRepository
                .findByLocationIgnoreCase(location, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<InternshipDTO> filterByType(InternshipType type, int page, int size) {

        log.info("Filtering internships by type={}", type);

        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size)
        );

        return internshipRepository
                .findByType(type, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<InternshipDTO> search(String keyword, int page, int size) {

        log.info("Searching internships | keyword={}", keyword);

        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size)
        );

        return internshipRepository
                .searchInternships(keyword, pageable)
                .map(this::mapToDto);
    }

    /* ============================================================
       MAPPING — MATCHED EXACTLY TO InternshipDTO
       ============================================================ */

    private InternshipDTO mapToDto(Internship internship) {

        InternshipDTO dto = new InternshipDTO();

        dto.setId(internship.getId());
        dto.setCompanyName(internship.getCompanyName());
        dto.setRole(internship.getRole());
        dto.setLocation(internship.getLocation());
        dto.setType(internship.getType());
        dto.setStipend(internship.getStipend());
        dto.setDuration(internship.getDuration());
        dto.setSkillsRequired(internship.getSkillsRequired());
        dto.setApplyLink(internship.getApplyLink());
        dto.setLastDateToApply(internship.getLastDateToApply());
        dto.setPostedOn(internship.getPostedOn());

        return dto;
    }

    private Pageable buildPageable(int page, int size, String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }

        return PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(sortBy).descending()
        );
    }
}

