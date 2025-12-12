package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.InternshipDTO;
import com.example.CampusConnect.model.InternshipType;
import com.example.CampusConnect.model.Internship;
import com.example.CampusConnect.repository.InternshipRepository;
import com.example.CampusConnect.service.InternshipUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternshipUserServiceImpl implements InternshipUserService {

    private final InternshipRepository internshipRepository;

    @Override
    public Page<InternshipDTO> getAllInternships(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return internshipRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public Page<InternshipDTO> filterByLocation(String location, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return internshipRepository.findByLocationIgnoreCase(location, pageable).map(this::mapToDto);
    }

    @Override
    public Page<InternshipDTO> filterByType(InternshipType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return internshipRepository.findByType(type, pageable).map(this::mapToDto);
    }

    @Override
    public Page<InternshipDTO> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return internshipRepository.searchInternships(keyword, pageable).map(this::mapToDto);
    }

    private InternshipDTO mapToDto(Internship internship) {
        InternshipDTO dto = new InternshipDTO();
        BeanUtils.copyProperties(internship, dto);
        return dto;
    }
}
