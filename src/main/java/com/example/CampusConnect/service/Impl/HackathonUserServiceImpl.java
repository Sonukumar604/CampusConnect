package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.HackathonDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Hackathon;
import com.example.CampusConnect.repository.HackathonRepository;
import com.example.CampusConnect.service.HackathonUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")     // Restrict entire service to USER
@Transactional(readOnly = true)     //  Optimized for read-only
public class HackathonUserServiceImpl implements HackathonUserService {

    private static final Logger log =
            LoggerFactory.getLogger(HackathonUserServiceImpl.class);

    private final HackathonRepository hackathonRepository;
    private final ModelMapper modelMapper;

    /* ============================================================
       NON-PAGED LIST
       ============================================================ */

    @Override
    public List<HackathonDTO> getAllHackathons() {

        log.info("Fetching all hackathons (non-paged)");

        return hackathonRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* ============================================================
       NON-PAGED FILTER
       ============================================================ */

    @Override
    public List<HackathonDTO> filterHackathons(
            String technology,
            String organization,
            LocalDate startDate) {

        log.info("Filtering hackathons | technology={}, organization={}, startDate={}",
                technology, organization, startDate);

        Page<Hackathon> page = hackathonRepository.filterHackathons(
                technology,
                organization,
                startDate,
                Pageable.unpaged()
        );

        return page.getContent()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* ============================================================
       GET BY ID
       ============================================================ */

    @Override
    public HackathonDTO getHackathonById(Long hackathonId) {

        log.info("Fetching hackathon details | id={}", hackathonId);

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hackathon not found with id " + hackathonId)
                );

        return toDto(hackathon);
    }

    /* ============================================================
       PAGED LIST
       ============================================================ */

    @Override
    public Page<HackathonDTO> getAllHackathonsPaged(
            int page,
            int size,
            String sortBy,
            String direction) {

        log.info("Fetching paged hackathons | page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction);

        Pageable pageable = buildPageable(page, size, sortBy, direction);

        return hackathonRepository.findAll(pageable)
                .map(this::toDto);
    }

    /* ============================================================
       PAGED FILTER
       ============================================================ */

    @Override
    public Page<HackathonDTO> filterHackathonsPaged(
            String technology,
            String organization,
            LocalDate startDate,
            int page,
            int size,
            String sortBy,
            String direction) {

        log.info("Filtering hackathons (paged) | technology={}, organization={}, startDate={}",
                technology, organization, startDate);

        Pageable pageable = buildPageable(page, size, sortBy, direction);

        return hackathonRepository
                .filterHackathons(technology, organization, startDate, pageable)
                .map(this::toDto);
    }

    /* ============================================================
       HELPER METHODS
       ============================================================ */

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }

        boolean asc = !"desc".equalsIgnoreCase(direction);

        Sort sort = asc
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                sort
        );
    }

    private HackathonDTO toDto(Hackathon hackathon) {
        return modelMapper.map(hackathon, HackathonDTO.class);
    }
}
