package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.ScholarshipDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.*;
import com.example.CampusConnect.repository.ScholarshipRepository;
import com.example.CampusConnect.service.ScholarshipUserService;
import com.example.CampusConnect.util.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")         // USER only
@Transactional(readOnly = true)          // Read-only optimization
public class ScholarshipUserServiceImpl implements ScholarshipUserService {

    private static final Logger log =
            LoggerFactory.getLogger(ScholarshipUserServiceImpl.class);

    private final ScholarshipRepository scholarshipRepository;
    private final ModelMapper mapper;

    /* ============================================================
       LIST PUBLISHED
       ============================================================ */

    @Override
    public PagedResponse<ScholarshipDTO> listPublishedScholarships(
            int page,
            int size,
            String sortBy,
            String sortDir) {

        log.info("Fetching published scholarships | page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);

        Page<Scholarship> result =
                scholarshipRepository.findByPublishStatus(PublishStatus.PUBLISHED, pageable);

        return buildPagedResponse(result);
    }

    /* ============================================================
       FILTER SCHOLARSHIPS
       ============================================================ */

    @Override
    public PagedResponse<ScholarshipDTO> filterScholarships(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String category,
            String provider,
            Double minAmount,
            Double maxAmount,
            LocalDate deadlineBefore,
            LocalDate deadlineAfter,
            String eligibilityContains) {

        log.info("Filtering scholarships | category={}, provider={}",
                category, provider);

        Pageable pageable = buildPageable(0, Integer.MAX_VALUE, sortBy, sortDir);

        Page<Scholarship> allPublished =
                scholarshipRepository.findByPublishStatus(PublishStatus.PUBLISHED, pageable);

        Stream<Scholarship> stream = allPublished.getContent().stream();

        // Category filter
        if (category != null && !category.isBlank()) {
            try {
                ScholarshipCategory cat = ScholarshipCategory.valueOf(category);
                stream = stream.filter(s -> s.getCategory() == cat);
            } catch (IllegalArgumentException ignored) {
                log.warn("Invalid scholarship category ignored: {}", category);
            }
        }

        // Provider filter
        if (provider != null && !provider.isBlank()) {
            stream = stream.filter(s ->
                    s.getProvider() != null &&
                            s.getProvider().toLowerCase().contains(provider.toLowerCase()));
        }

        // Amount filters
        if (minAmount != null) {
            stream = stream.filter(s -> s.getAmount() != null && s.getAmount() >= minAmount);
        }

        if (maxAmount != null) {
            stream = stream.filter(s -> s.getAmount() != null && s.getAmount() <= maxAmount);
        }

        // Deadline filters
        if (deadlineBefore != null) {
            stream = stream.filter(s ->
                    s.getDeadline() != null &&
                            s.getDeadline().isBefore(deadlineBefore));
        }

        if (deadlineAfter != null) {
            stream = stream.filter(s ->
                    s.getDeadline() != null &&
                            s.getDeadline().isAfter(deadlineAfter));
        }

        // Eligibility filter
        if (eligibilityContains != null && !eligibilityContains.isBlank()) {
            stream = stream.filter(s ->
                    s.getEligibilityCriteria() != null &&
                            s.getEligibilityCriteria().toLowerCase()
                                    .contains(eligibilityContains.toLowerCase()));
        }

        List<Scholarship> filtered = stream.collect(Collectors.toList());

        int safeSize = Math.max(1, size);
        int from = Math.max(0, page) * safeSize;
        int to = Math.min(from + safeSize, filtered.size());

        List<Scholarship> pageContent =
                from >= filtered.size()
                        ? Collections.emptyList()
                        : filtered.subList(from, to);

        log.info("Scholarship filter result | total={}, returned={}",
                filtered.size(), pageContent.size());

        return new PagedResponse<>(
                pageContent.stream().map(this::toDto).collect(Collectors.toList()),
                page,
                safeSize,
                filtered.size(),
                (int) Math.ceil((double) filtered.size() / safeSize),
                to >= filtered.size()
        );
    }

    /* ============================================================
       GET DETAILS
       ============================================================ */

    @Override
    public ScholarshipDTO getScholarshipDetails(Long id) {

        log.info("Fetching scholarship details | id={}", id);

        Scholarship scholarship = scholarshipRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Scholarship not found"));

        return toDto(scholarship);
    }

    /* ============================================================
       HELPERS
       ============================================================ */

    private Pageable buildPageable(int page,
                                   int size,
                                   String sortBy,
                                   String sortDir) {

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }

        Sort.Direction direction =
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(direction, sortBy)
        );
    }

    private PagedResponse<ScholarshipDTO> buildPagedResponse(Page<Scholarship> page) {

        return new PagedResponse<>(
                page.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    private ScholarshipDTO toDto(Scholarship e) {

        ScholarshipDTO dto = mapper.map(e, ScholarshipDTO.class);

        dto.setCreatedById(
                e.getCreatedByUser() != null ? e.getCreatedByUser().getId() : null
        );

        dto.setCreatedByName(
                e.getCreatedByUser() != null ? e.getCreatedByUser().getName() : null
        );

        dto.setApplicationCount(
                e.getApplications() != null ? e.getApplications().size() : 0
        );

        dto.setCategory(
                e.getCategory() != null ? e.getCategory().name() : null
        );

        dto.setPublishStatus(
                e.getPublishStatus() != null ? e.getPublishStatus().name() : null
        );

        return dto;
    }
}
