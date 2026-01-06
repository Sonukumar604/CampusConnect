
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScholarshipApplicationUserServiceImpl implements ScholarshipApplicationUserService {

    private final ScholarshipApplicationRepository applicationRepo;
    private final ScholarshipRepository scholarshipRepo;
    private final UserRepository userRepo;
    private final ModelMapper mapper;

    @Override
    public ScholarshipApplicationResponseDTO apply(Long userId, CreateScholarshipApplicationRequest request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Scholarship scholarship = scholarshipRepo.findById(request.getScholarshipId())
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found"));

        ScholarshipApplication application = new ScholarshipApplication();
        application.setUser(user);
        application.setScholarship(scholarship);
        application.setSop(request.getStatementOfPurpose());
        application.setStatus(ApplicationStatus.PENDING);

        ScholarshipApplication saved = applicationRepo.save(application);

        return mapper.map(saved, ScholarshipApplicationResponseDTO.class);
    }

    @Override
    public List<ScholarshipApplicationResponseDTO> getMyApplications(Long userId) {
        return applicationRepo.findByUserId(userId)
                .stream()
                .map(a -> mapper.map(a, ScholarshipApplicationResponseDTO.class))
                .toList();
    }
}
