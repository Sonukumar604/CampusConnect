package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.*;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.*;
import com.example.CampusConnect.repository.*;
import com.example.CampusConnect.service.ScholarshipAdminService;
import com.example.CampusConnect.util.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")   // 🔐 ADMIN ONLY
@Transactional
public class ScholarshipAdminServiceImpl implements ScholarshipAdminService {

    private static final Logger log =
            LoggerFactory.getLogger(ScholarshipAdminServiceImpl.class);

    private final ScholarshipRepository scholarshipRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    // =========================
    // CREATE
    // =========================
    @Override
    public ScholarshipDTO createScholarship(Long adminId, CreateScholarshipDTO dto) {

        log.info("Admin [{}] requested scholarship creation | title='{}'",
                adminId, dto.getTitle());

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> {
                    log.error("Admin not found while creating scholarship | adminId={}", adminId);
                    return new ResourceNotFoundException("Admin not found");
                });

        ScholarshipCategory category;
        try {
            category = ScholarshipCategory.valueOf(dto.getCategory().toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.error("Invalid scholarship category '{}'", dto.getCategory());
            throw new IllegalArgumentException("Invalid scholarship category");
        }

        Scholarship s = Scholarship.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(category)
                .eligibilityCriteria(dto.getEligibilityCriteria())
                .amount(dto.getAmount())
                .deadline(dto.getDeadline())
                .provider(dto.getProvider())
                .publishStatus(PublishStatus.DRAFT)
                .createdByUser(admin)
                .build();

        Scholarship saved = scholarshipRepository.save(s);

        log.info("Scholarship created successfully | scholarshipId={}", saved.getId());

        return toDto(saved);
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public ScholarshipDTO updateScholarship(Long adminId,
                                            Long scholarshipId,
                                            UpdateScholarshipDTO dto) {

        log.info("Admin [{}] updating scholarship [{}]", adminId, scholarshipId);

        Scholarship s = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> {
                    log.error("Scholarship not found while updating | id={}", scholarshipId);
                    return new ResourceNotFoundException("Scholarship not found");
                });

        try {
            s.setCategory(
                    ScholarshipCategory.valueOf(dto.getCategory().toUpperCase())
            );
        } catch (IllegalArgumentException ex) {
            log.error("Invalid scholarship category '{}'", dto.getCategory());
            throw new IllegalArgumentException("Invalid scholarship category");
        }

        s.setTitle(dto.getTitle());
        s.setDescription(dto.getDescription());
        s.setEligibilityCriteria(dto.getEligibilityCriteria());
        s.setAmount(dto.getAmount());
        s.setDeadline(dto.getDeadline());
        s.setProvider(dto.getProvider());

        scholarshipRepository.save(s);

        log.info("Scholarship updated successfully | scholarshipId={}", scholarshipId);

        return toDto(s);
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void deleteScholarship(Long adminId, Long scholarshipId) {

        log.warn("Admin [{}] deleting scholarship [{}]", adminId, scholarshipId);

        Scholarship s = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> {
                    log.error("Scholarship not found while deleting | id={}", scholarshipId);
                    return new ResourceNotFoundException("Scholarship not found");
                });

        scholarshipRepository.delete(s);

        log.info("Scholarship deleted successfully | scholarshipId={}", scholarshipId);
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    @Transactional(readOnly = true)
    public ScholarshipDTO getScholarshipById(Long scholarshipId) {

        log.info("Fetching scholarship details | scholarshipId={}", scholarshipId);

        Scholarship s = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> {
                    log.error("Scholarship not found | id={}", scholarshipId);
                    return new ResourceNotFoundException("Scholarship not found");
                });

        return toDto(s);
    }

    // =========================
    // PAGINATION
    // =========================
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ScholarshipDTO> getScholarshipsPaged(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String category) {

        log.info(
                "Fetching scholarships (paged) | page={}, size={}, sortBy={}, sortDir={}, category={}",
                page, size, sortBy, sortDir, category
        );

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Scholarship> p;

        if (category != null && !category.isBlank()) {
            try {
                p = scholarshipRepository.findByCategory(
                        ScholarshipCategory.valueOf(category.toUpperCase()),
                        pageable);
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid scholarship category '{}', returning all", category);
                p = scholarshipRepository.findAll(pageable);
            }
        } else {
            p = scholarshipRepository.findAll(pageable);
        }

        log.info("Fetched {} scholarships (totalElements={})",
                p.getNumberOfElements(), p.getTotalElements());

        return new PagedResponse<>(
                p.getContent()
                        .stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()),
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages(),
                p.isLast()
        );
    }

    // =========================
    // DTO MAPPER
    // =========================
    private ScholarshipDTO toDto(Scholarship e) {

        ScholarshipDTO dto = mapper.map(e, ScholarshipDTO.class);

        dto.setCreatedById(
                e.getCreatedByUser() != null
                        ? e.getCreatedByUser().getId()
                        : null
        );

        dto.setCreatedByName(
                e.getCreatedByUser() != null
                        ? e.getCreatedByUser().getName()
                        : null
        );

        dto.setApplicationCount(
                e.getApplications() != null
                        ? e.getApplications().size()
                        : 0
        );

        dto.setCategory(
                e.getCategory() != null
                        ? e.getCategory().name()
                        : null
        );

        dto.setPublishStatus(
                e.getPublishStatus() != null
                        ? e.getPublishStatus().name()
                        : null
        );

        return dto;
    }
}
