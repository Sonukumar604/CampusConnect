package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.ScholarshipApplicationResponseDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.ApplicationStatus;
import com.example.CampusConnect.model.ScholarshipApplication;
import com.example.CampusConnect.repository.ScholarshipApplicationRepository;
import com.example.CampusConnect.repository.ScholarshipRepository;
import com.example.CampusConnect.service.ScholarshipApplicationAdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScholarshipApplicationAdminServiceImpl
        implements ScholarshipApplicationAdminService {

    private static final Logger log =
            LoggerFactory.getLogger(ScholarshipApplicationAdminServiceImpl.class);

    private final ScholarshipApplicationRepository applicationRepo;
    private final ScholarshipRepository scholarshipRepo;
    private final ModelMapper mapper;

    @Override
    public List<ScholarshipApplicationResponseDTO> getApplicationsForScholarship(Long scholarshipId) {

        log.info("Fetching applications for scholarship | scholarshipId={}", scholarshipId);

        scholarshipRepo.findById(scholarshipId)
                .orElseThrow(() -> {
                    log.error("Scholarship not found while fetching applications | id={}", scholarshipId);
                    return new ResourceNotFoundException("Scholarship not found");
                });

        List<ScholarshipApplicationResponseDTO> responses =
                applicationRepo.findByScholarshipId(scholarshipId)
                        .stream()
                        .map(a -> mapper.map(a, ScholarshipApplicationResponseDTO.class))
                        .toList();

        log.info("Fetched {} applications for scholarship | scholarshipId={}",
                responses.size(), scholarshipId);

        return responses;
    }

    @Override
    public ScholarshipApplicationResponseDTO updateStatus(Long applicationId, String status) {

        log.info("Updating scholarship application status | applicationId={}, newStatus={}",
                applicationId, status);

        ScholarshipApplication application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> {
                    log.error("Scholarship application not found | applicationId={}", applicationId);
                    return new ResourceNotFoundException("Application not found");
                });

        application.setStatus(ApplicationStatus.valueOf(status));

        ScholarshipApplication updated = applicationRepo.save(application);

        log.info("Scholarship application status updated successfully | applicationId={}, status={}",
                applicationId, updated.getStatus());

        return mapper.map(updated, ScholarshipApplicationResponseDTO.class);
    }
}
