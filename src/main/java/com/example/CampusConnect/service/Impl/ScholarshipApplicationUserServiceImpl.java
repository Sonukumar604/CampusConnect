package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CreateScholarshipApplicationRequest;
import com.example.CampusConnect.dto.ScholarshipApplicationResponseDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.ApplicationStatus;
import com.example.CampusConnect.model.Scholarship;
import com.example.CampusConnect.model.ScholarshipApplication;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.ScholarshipApplicationRepository;
import com.example.CampusConnect.repository.ScholarshipRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.ScholarshipApplicationUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScholarshipApplicationUserServiceImpl
        implements ScholarshipApplicationUserService {

    private static final Logger log =
            LoggerFactory.getLogger(ScholarshipApplicationUserServiceImpl.class);

    private final ScholarshipApplicationRepository applicationRepo;
    private final ScholarshipRepository scholarshipRepo;
    private final UserRepository userRepo;
    private final ModelMapper mapper;

    @Override
    public ScholarshipApplicationResponseDTO apply(
            Long userId,
            CreateScholarshipApplicationRequest request) {

        log.info("Applying for scholarship | userId={}, scholarshipId={}",
                userId, request.getScholarshipId());

        User user = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found while applying for scholarship | userId={}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        Scholarship scholarship = scholarshipRepo.findById(request.getScholarshipId())
                .orElseThrow(() -> {
                    log.error("Scholarship not found while applying | scholarshipId={}",
                            request.getScholarshipId());
                    return new ResourceNotFoundException("Scholarship not found");
                });

        ScholarshipApplication application = new ScholarshipApplication();
        application.setUser(user);
        application.setScholarship(scholarship);
        application.setSop(request.getStatementOfPurpose());
        application.setStatus(ApplicationStatus.PENDING);

        ScholarshipApplication saved = applicationRepo.save(application);

        log.info("Scholarship application submitted successfully | applicationId={}, userId={}",
                saved.getId(), userId);

        return mapper.map(saved, ScholarshipApplicationResponseDTO.class);
    }

    @Override
    public List<ScholarshipApplicationResponseDTO> getMyApplications(Long userId) {

        log.info("Fetching scholarship applications for user | userId={}", userId);

        List<ScholarshipApplicationResponseDTO> applications =
                applicationRepo.findByUserId(userId)
                        .stream()
                        .map(a -> mapper.map(a, ScholarshipApplicationResponseDTO.class))
                        .toList();

        log.info("Found {} scholarship applications for user | userId={}",
                applications.size(), userId);

        return applications;
    }
}
