package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.*;

import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.*;
import com.example.CampusConnect.repository.*;
import com.example.CampusConnect.service.ScholarshipAdminService;
import com.example.CampusConnect.util.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScholarshipAdminServiceImpl implements ScholarshipAdminService {

    private final ScholarshipRepository scholarshipRepository;
    private final com.example.CampusConnect.repository.UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public ScholarshipDTO createScholarship(Long adminId, CreateScholarshipDTO dto) {
        com.example.CampusConnect.model.User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        Scholarship s = Scholarship.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(ScholarshipCategory.valueOf(dto.getCategory()))
                .eligibilityCriteria(dto.getEligibilityCriteria())
                .amount(dto.getAmount())
                .deadline(dto.getDeadline())
                .provider(dto.getProvider())
                .publishStatus(PublishStatus.DRAFT)
                .createdByUser(admin)
                .build();
        Scholarship saved = scholarshipRepository.save(s);
        return toDto(saved);
    }

    @Override
    public ScholarshipDTO updateScholarship(Long adminId, Long scholarshipId, UpdateScholarshipDTO dto) {
        Scholarship s = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found"));
        // optional: check admin ownership/role
        s.setTitle(dto.getTitle());
        s.setDescription(dto.getDescription());
        s.setCategory(ScholarshipCategory.valueOf(dto.getCategory()));
        s.setEligibilityCriteria(dto.getEligibilityCriteria());
        s.setAmount(dto.getAmount());
        s.setDeadline(dto.getDeadline());
        s.setProvider(dto.getProvider());
        scholarshipRepository.save(s);
        return toDto(s);
    }

    @Override
    public void deleteScholarship(Long adminId, Long scholarshipId) {
        Scholarship s = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found"));
        scholarshipRepository.delete(s);
    }

    @Override
    public ScholarshipDTO getScholarshipById(Long scholarshipId) {
        Scholarship s = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found"));
        return toDto(s);
    }

    @Override
    public PagedResponse<ScholarshipDTO> getScholarshipsPaged(int page, int size, String sortBy, String sortDir, String category) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Scholarship> p;
        if (category != null && !category.isBlank()) {
            try {
                p = scholarshipRepository.findByCategory(ScholarshipCategory.valueOf(category), pageable);
            } catch (IllegalArgumentException ex) {
                p = scholarshipRepository.findAll(pageable);
            }
        } else {
            p = scholarshipRepository.findAll(pageable);
        }
        return new PagedResponse<>(
                p.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.isLast()
        );
    }

    // mapper
    private ScholarshipDTO toDto(Scholarship e) {
        ScholarshipDTO dto = mapper.map(e, ScholarshipDTO.class);
        dto.setCreatedById(e.getCreatedBy() != null ? e.getCreatedByUser().getId() : null);
        dto.setCreatedByName(e.getCreatedBy() != null ? e.getCreatedByUser().getName() : null);
        dto.setApplicationCount(e.getApplications() != null ? e.getApplications().size() : 0);
        dto.setCategory(e.getCategory() != null ? e.getCategory().name() : null);
        dto.setPublishStatus(e.getPublishStatus() != null ? e.getPublishStatus().name() : null);
        return dto;
    }
}
