package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.*;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.*;
import com.example.CampusConnect.repository.ScholarshipRepository;
import com.example.CampusConnect.service.ScholarshipUserService;
import com.example.CampusConnect.util.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScholarshipUserServiceImpl implements ScholarshipUserService {

    private final ScholarshipRepository scholarshipRepository;
    private final ModelMapper mapper;

    @Override
    public PagedResponse<ScholarshipDTO> listPublishedScholarships(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Scholarship> p = scholarshipRepository.findByPublishStatus(PublishStatus.PUBLISHED, pageable);
        return new PagedResponse<>(
                p.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.isLast()
        );
    }

    @Override
    public PagedResponse<ScholarshipDTO> filterScholarships(int page, int size, String sortBy, String sortDir, String category, String provider, Double minAmount, Double maxAmount, LocalDate deadlineBefore, LocalDate deadlineAfter, String eligibilityContains) {
        // For simplicity: start from all published then filter in-memory.
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, sort); // get all then apply manual paging & filters
        Page<Scholarship> allPublished = scholarshipRepository.findByPublishStatus(PublishStatus.PUBLISHED, pageable);

        var stream = allPublished.getContent().stream();

        if (category != null && !category.isBlank()) {
            try {
                ScholarshipCategory cat = ScholarshipCategory.valueOf(category);
                stream = stream.filter(s -> s.getCategory() == cat);
            } catch (IllegalArgumentException ignored) {}
        }
        if (provider != null && !provider.isBlank()) {
            stream = stream.filter(s -> s.getProvider() != null && s.getProvider().toLowerCase().contains(provider.toLowerCase()));
        }
        if (minAmount != null) stream = stream.filter(s -> s.getAmount() != null && s.getAmount() >= minAmount);
        if (maxAmount != null) stream = stream.filter(s -> s.getAmount() != null && s.getAmount() <= maxAmount);
        if (deadlineBefore != null) stream = stream.filter(s -> s.getDeadline() != null && s.getDeadline().isBefore(deadlineBefore));
        if (deadlineAfter != null) stream = stream.filter(s -> s.getDeadline() != null && s.getDeadline().isAfter(deadlineAfter));
        if (eligibilityContains != null && !eligibilityContains.isBlank()) {
            stream = stream.filter(s -> s.getEligibilityCriteria() != null && s.getEligibilityCriteria().toLowerCase().contains(eligibilityContains.toLowerCase()));
        }

        var list = stream.collect(Collectors.toList());
        // paging manually
        int from = page * size;
        int to = Math.min(from + size, list.size());
        var pageContent = from > list.size() ? java.util.Collections.<Scholarship>emptyList() : list.subList(from, to);

        return new PagedResponse<>(
                pageContent.stream().map(this::toDto).collect(Collectors.toList()),
                page, size, list.size(), (int)Math.ceil((double)list.size()/size), (to == list.size())
        );
    }

    @Override
    public ScholarshipDTO getScholarshipDetails(Long id) {
        Scholarship s = scholarshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found"));
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

