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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")   // Only USER can register
@Transactional                     // Write-heavy service
public class HackathonRegistrationServiceImpl
        implements HackathonRegistrationService {

    private static final Logger log =
            LoggerFactory.getLogger(HackathonRegistrationServiceImpl.class);

    private final HackathonRegistrationRepository registrationRepository;
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    /* ============================================================
       REGISTER USER
       ============================================================ */

    @Override
    @OptimisticRetry // ✅ automatic retry on version conflict
    public HackathonRegistrationDTO registerUser(
            Long userId,
            Long hackathonId,
            RegistrationHackathonRequest request) {

        log.info("User {} attempting to register for hackathon {}", userId, hackathonId);

        // Prevent duplicate registration
        if (registrationRepository
                .existsByUser_IdAndHackathon_Id(userId, hackathonId)) {
            throw new IllegalArgumentException("Already registered");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Pessimistic lock query
        Hackathon hackathon = hackathonRepository.findByIdForUpdate(hackathonId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hackathon not found"));

        if (!hackathon.isRegistrationsOpen()) {
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

        HackathonRegistration saved =
                registrationRepository.save(registration);

        log.info("User {} successfully registered for hackathon {}",
                userId, hackathonId);

        return mapper.map(saved, HackathonRegistrationDTO.class);
    }

    /* ============================================================
       GET USER REGISTRATIONS
       ============================================================ */

    @Override
    @Transactional(readOnly = true)
    public List<HackathonRegistrationDTO> getRegistrationsByUser(Long userId) {

        log.info("Fetching hackathon registrations for userId={}", userId);

        return registrationRepository.findByUser_Id(userId)
                .stream()
                .map(r -> mapper.map(r, HackathonRegistrationDTO.class))
                .collect(Collectors.toList());
    }
}
