package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.HackathonDTO;
import com.example.CampusConnect.model.Hackathon;
import com.example.CampusConnect.repository.HackathonRepository;
import com.example.CampusConnect.service.HackathonUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HackathonUserServiceImpl implements HackathonUserService {

    private final HackathonRepository hackathonRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<HackathonDTO> getAllHackathons() {
        List<Hackathon> list = hackathonRepository.findAll();
        return list.stream()
                .map(h -> modelMapper.map(h, HackathonDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Non-paged filter. The repository method expects a Pageable,
     * so we call it with Pageable.unpaged() and return the page content.
     */
    @Override
    public List<HackathonDTO> filterHackathons(String technology, String organization, LocalDate startDate) {
        Page<Hackathon> page = hackathonRepository.filterHackathons(
                technology, organization, startDate, Pageable.unpaged());
        return page.getContent().stream()
                .map(h -> modelMapper.map(h, HackathonDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public HackathonDTO getHackathonById(Long hackathonId) {
        Hackathon hack = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found with id " + hackathonId));
        return modelMapper.map(hack, HackathonDTO.class);
    }

    /**
     * Paged listing for user side. Supports sorting direction & field.
     */
    @Override
    public Page<HackathonDTO> getAllHackathonsPaged(int page, int size, String sortBy, String direction) {
        Sort sort = buildSort(sortBy, direction);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
        Page<Hackathon> p = hackathonRepository.findAll(pageable);
        return p.map(h -> modelMapper.map(h, HackathonDTO.class));
    }

    /**
     * Paged filter. Delegates to repository's filter query (which accepts Pageable).
     */
    @Override
    public Page<HackathonDTO> filterHackathonsPaged(String technology,
                                                    String organization,
                                                    LocalDate startDate,
                                                    int page, int size, String sortBy, String direction) {
        Sort sort = buildSort(sortBy, direction);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
        Page<Hackathon> p = hackathonRepository.filterHackathons(technology, organization, startDate, pageable);
        return p.map(h -> modelMapper.map(h, HackathonDTO.class));
    }

    // Helper to build Sort safely (defaults to id asc if invalid)
    private Sort buildSort(String sortBy, String direction) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }
        boolean asc = !"desc".equalsIgnoreCase(direction);
        return asc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    }
}
