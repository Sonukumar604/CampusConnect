package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.InternshipDTO;
import com.example.CampusConnect.model.InternshipType;
import com.example.CampusConnect.model.Internship;
import com.example.CampusConnect.repository.InternshipRepository;
import com.example.CampusConnect.service.InternshipUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternshipUserServiceImpl implements InternshipUserService {

    private static final Logger log =
            LoggerFactory.getLogger(InternshipUserServiceImpl.class);

    private final InternshipRepository internshipRepository;

    @Override
    public Page<InternshipDTO> getAllInternships(int page, int size, String sortBy) {

        log.info("Fetching all internships | page={}, size={}, sortBy={}", page, size, sortBy);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<InternshipDTO> result =
                internshipRepository.findAll(pageable).map(this::mapToDto);

        log.info("Fetched {} internships", result.getNumberOfElements());

        return result;
    }

    @Override
    public Page<InternshipDTO> filterByLocation(String location, int page, int size) {

        log.info("Filtering internships by location='{}' | page={}, size={}", location, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<InternshipDTO> result =
                internshipRepository.findByLocationIgnoreCase(location, pageable)
                        .map(this::mapToDto);

        log.info("Found {} internships for location='{}'",
                result.getNumberOfElements(), location);

        return result;
    }

    @Override
    public Page<InternshipDTO> filterByType(InternshipType type, int page, int size) {

        log.info("Filtering internships by type={} | page={}, size={}", type, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<InternshipDTO> result =
                internshipRepository.findByType(type, pageable)
                        .map(this::mapToDto);

        log.info("Found {} internships for type={}",
                result.getNumberOfElements(), type);

        return result;
    }

    @Override
    public Page<InternshipDTO> search(String keyword, int page, int size) {

        log.info("Searching internships with keyword='{}' | page={}, size={}",
                keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<InternshipDTO> result =
                internshipRepository.searchInternships(keyword, pageable)
                        .map(this::mapToDto);

        log.info("Search returned {} internships for keyword='{}'",
                result.getNumberOfElements(), keyword);

        return result;
    }

    private InternshipDTO mapToDto(Internship internship) {
        InternshipDTO dto = new InternshipDTO();
        BeanUtils.copyProperties(internship, dto);
        return dto;
    }
}
