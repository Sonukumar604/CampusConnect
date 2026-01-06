
package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.ScholarshipApplicationResponseDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.ApplicationStatus;
import com.example.CampusConnect.model.Scholarship;
import com.example.CampusConnect.model.ScholarshipApplication;
import com.example.CampusConnect.repository.ScholarshipApplicationRepository;
import com.example.CampusConnect.repository.ScholarshipRepository;
import com.example.CampusConnect.service.ScholarshipApplicationAdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScholarshipApplicationAdminServiceImpl implements ScholarshipApplicationAdminService {

    private final ScholarshipApplicationRepository applicationRepo;
    private final ScholarshipRepository scholarshipRepo;
    private final ModelMapper mapper;

    @Override
    public List<ScholarshipApplicationResponseDTO> getApplicationsForScholarship(Long scholarshipId) {

        scholarshipRepo.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found"));

        return applicationRepo.findByScholarshipId(scholarshipId)
                .stream()
                .map(a -> mapper.map(a, ScholarshipApplicationResponseDTO.class))
                .toList();
    }

    @Override
    public ScholarshipApplicationResponseDTO updateStatus(Long applicationId, String status) {

        ScholarshipApplication application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        application.setStatus(ApplicationStatus.valueOf(status));

        ScholarshipApplication updated = applicationRepo.save(application);

        return mapper.map(updated, ScholarshipApplicationResponseDTO.class);
    }
}
