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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HackathonRegistrationServiceImpl
        implements HackathonRegistrationService {

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
    public HackathonRegistrationDTO registerUser(
            Long userId,
            Long hackathonId,
            RegistrationHackathonRequest request) {

        if (registrationRepository.existsByUser_IdAndHackathon_Id(userId, hackathonId)) {
            throw new IllegalArgumentException("Already registered");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Hackathon hackathon = hackathonRepository.findByIdForUpdate(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));

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

        HackathonRegistration saved = registrationRepository.save(registration);
        return mapper.map(saved, HackathonRegistrationDTO.class);
    }

    @Override
    public List<HackathonRegistrationDTO> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUser_Id(userId)
                .stream()
                .map(r -> mapper.map(r, HackathonRegistrationDTO.class))
                .collect(Collectors.toList());
    }
}
