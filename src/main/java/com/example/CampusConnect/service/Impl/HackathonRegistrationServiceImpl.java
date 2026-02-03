package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.HackathonRegistrationDTO;
import com.example.CampusConnect.dto.RegistrationHackathonRequest;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Hackathon;
import com.example.CampusConnect.model.HackathonRegistration;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.HackathonRegistrationRepository;
import com.example.CampusConnect.repository.HackathonRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.HackathonRegistrationService;
import com.example.CampusConnect.util.retry.OptimisticRetry;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HackathonRegistrationServiceImpl
        implements HackathonRegistrationService {

    private static final Logger log =
            LoggerFactory.getLogger(HackathonRegistrationServiceImpl.class);

    @Autowired
    private HackathonRegistrationRepository registrationRepository;

    @Autowired
    private HackathonRepository hackathonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    @Transactional
    @OptimisticRetry // ✅ automatic retry on version conflict
    public HackathonRegistrationDTO registerUser(
            Long userId,
            Long hackathonId,
            RegistrationHackathonRequest request) {

        log.info("User {} attempting to register for hackathon {}", userId, hackathonId);

        if (registrationRepository.existsByUser_IdAndHackathon_Id(userId, hackathonId)) {
            log.warn("Registration failed: user {} already registered for hackathon {}", userId, hackathonId);
            throw new IllegalArgumentException("Already registered");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Registration failed: user not found with id={}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        Hackathon hackathon = hackathonRepository.findByIdForUpdate(hackathonId)
                .orElseThrow(() -> {
                    log.error("Registration failed: hackathon not found with id={}", hackathonId);
                    return new ResourceNotFoundException("Hackathon not found");
                });

        if (!hackathon.isRegistrationsOpen()) {
            log.warn("Registration closed for hackathon {}", hackathonId);
            throw new IllegalStateException("Registrations closed");
        }

        HackathonRegistration registration = HackathonRegistration.builder()
                .user(user)
                .hackathon(hackathon)
                .teamName(request.getTeamName())
                .projectIdea(request.getProjectIdea())
                .approved(false)
                .registeredAt(LocalDateTime.now())
                .registrationDate(LocalDateTime.now())
                .build();

        HackathonRegistration saved = registrationRepository.save(registration);

        log.info("User {} successfully registered for hackathon {}", userId, hackathonId);

        return mapper.map(saved, HackathonRegistrationDTO.class);
    }

    @Override
    public List<HackathonRegistrationDTO> getRegistrationsByUser(Long userId) {

        log.info("Fetching hackathon registrations for userId={}", userId);

        List<HackathonRegistrationDTO> result =
                registrationRepository.findByUser_Id(userId)
                        .stream()
                        .map(r -> mapper.map(r, HackathonRegistrationDTO.class))
                        .collect(Collectors.toList());

        log.info("Found {} hackathon registrations for userId={}", result.size(), userId);

        return result;
    }
}
