package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.*;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScholarshipUserServiceImpl implements ScholarshipUserService {

    private static final Logger log =
            LoggerFactory.getLogger(ScholarshipUserServiceImpl.class);

    private final ScholarshipRepository scholarshipRepository;
    private final ModelMapper mapper;

    @Override
    public PagedResponse<ScholarshipDTO> listPublishedScholarships(
            int page, int size, String sortBy, String sortDir) {

        log.info("Fetching published scholarships | page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Scholarship> p =
                scholarshipRepository.findByPublishStatus(PublishStatus.PUBLISHED, pageable);

        log.info("Published scholarships fetched | count={}, page={}",
                p.getNumberOfElements(), page);

        return new PagedResponse<>(
                p.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.isLast()
        );
    }

    @Override
    public PagedResponse<ScholarshipDTO> filterScholarships(
            int page, int size, String sortBy, String sortDir,
            String category, String provider,
            Double minAmount, Double maxAmount,
            LocalDate deadlineBefore, LocalDate deadlineAfter,
            String eligibilityContains) {

        log.info("Filtering scholarships | page={}, size={}, category={}, provider={}",
                page, size, category, provider);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);

        Page<Scholarship> allPublished =
                scholarshipRepository.findByPublishStatus(PublishStatus.PUBLISHED, pageable);

        var stream = allPublished.getContent().stream();

        if (category != null && !category.isBlank()) {
            try {
                ScholarshipCategory cat = ScholarshipCategory.valueOf(category);
                stream = stream.filter(s -> s.getCategory() == cat);
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid scholarship category filter ignored: {}", category);
            }
        }

        if (provider != null && !provider.isBlank()) {
            stream = stream.filter(s ->
                    s.getProvider() != null &&
                            s.getProvider().toLowerCase().contains(provider.toLowerCase()));
        }

        if (minAmount != null)
            stream = stream.filter(s -> s.getAmount() != null && s.getAmount() >= minAmount);

        if (maxAmount != null)
            stream = stream.filter(s -> s.getAmount() != null && s.getAmount() <= maxAmount);

        if (deadlineBefore != null)
            stream = stream.filter(s -> s.getDeadline() != null && s.getDeadline().isBefore(deadlineBefore));

        if (deadlineAfter != null)
            stream = stream.filter(s -> s.getDeadline() != null && s.getDeadline().isAfter(deadlineAfter));

        if (eligibilityContains != null && !eligibilityContains.isBlank()) {
            stream = stream.filter(s ->
                    s.getEligibilityCriteria() != null &&
                            s.getEligibilityCriteria().toLowerCase()
                                    .contains(eligibilityContains.toLowerCase()));
        }

        var list = stream.collect(Collectors.toList());

        int from = page * size;
        int to = Math.min(from + size, list.size());

        var pageContent =
                from > list.size()
                        ? java.util.Collections.<Scholarship>emptyList()
                        : list.subList(from, to);

        log.info("Scholarship filter result | total={}, returned={}",
                list.size(), pageContent.size());

        return new PagedResponse<>(
                pageContent.stream().map(this::toDto).collect(Collectors.toList()),
                page, size, list.size(),
                (int) Math.ceil((double) list.size() / size),
                (to == list.size())
        );
    }

    @Override
    public ScholarshipDTO getScholarshipDetails(Long id) {

        log.info("Fetching scholarship details | scholarshipId={}", id);

        Scholarship s = scholarshipRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Scholarship not found | scholarshipId={}", id);
                    return new ResourceNotFoundException("Scholarship not found");
                });

        log.info("Scholarship details fetched successfully | scholarshipId={}", id);

        return toDto(s);
    }

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
