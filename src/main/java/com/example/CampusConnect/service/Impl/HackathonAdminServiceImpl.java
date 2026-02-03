package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CreateHackathonDTO;
import com.example.CampusConnect.dto.HackathonDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Hackathon;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.HackathonRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.HackathonAdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HackathonAdminServiceImpl implements HackathonAdminService {

    private static final Logger log =
            LoggerFactory.getLogger(HackathonAdminServiceImpl.class);

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional  // ✅ REQUIRED for optimistic locking
    public HackathonDTO createHackathon(CreateHackathonDTO dto, Long adminId) {

        log.info("Admin {} is creating a new hackathon", adminId);

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> {
                    log.error("Hackathon creation failed: Admin not found with id={}", adminId);
                    return new ResourceNotFoundException("Admin not found");
                });

        Hackathon h = mapper.map(dto, Hackathon.class);
        h.assignCreator(admin);
        h.setRegisteredParticipantsCount(0);
        h.setActive(true);
        h.setRegistrationsOpen(true);

        Hackathon saved = hackathonRepository.save(h);

        log.info("Hackathon created successfully with id={}", saved.getId());

        return mapper.map(saved, HackathonDTO.class);
    }

    @Override
    @Transactional  // ✅ REQUIRED
    public HackathonDTO updateHackathon(Long hackathonId, CreateHackathonDTO dto) {

        log.info("Updating hackathon with id={}", hackathonId);

        Hackathon existing = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> {
                    log.error("Hackathon update failed: Hackathon not found with id={}", hackathonId);
                    return new ResourceNotFoundException("Hackathon not found");
                });

        mapper.map(dto, existing); // updates fields safely

        Hackathon updated = hackathonRepository.save(existing);

        log.info("Hackathon updated successfully with id={}", updated.getId());

        return mapper.map(updated, HackathonDTO.class);
    }

    @Override
    public List<HackathonDTO> getAllHackathons() {

        log.info("Fetching all hackathons");

        List<HackathonDTO> result = hackathonRepository.findAll()
                .stream()
                .map(h -> mapper.map(h, HackathonDTO.class))
                .collect(Collectors.toList());

        log.info("Fetched {} hackathons", result.size());

        return result;
    }

    @Override
    public HackathonDTO getHackathonById(Long hackathonId) {

        log.info("Fetching hackathon details for id={}", hackathonId);

        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> {
                    log.warn("Hackathon not found with id={}", hackathonId);
                    return new ResourceNotFoundException("Hackathon not found");
                });

        return mapper.map(h, HackathonDTO.class);
    }

    @Override
    public String toggleHackathonStatus(Long hackathonId) {

        log.info("Toggling hackathon status for id={}", hackathonId);

        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> {
                    log.error("Toggle failed: Hackathon not found with id={}", hackathonId);
                    return new ResourceNotFoundException("Hackathon not found");
                });

        h.setActive(!h.isActive());
        hackathonRepository.save(h);

        log.info("Hackathon {} status changed to {}", hackathonId, h.isActive());

        return h.isActive() ? "Hackathon activated" : "Hackathon deactivated";
    }

    @Override
    public String deleteHackathon(Long hackathonId) {

        log.info("Deleting hackathon with id={}", hackathonId);

        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> {
                    log.error("Delete failed: Hackathon not found with id={}", hackathonId);
                    return new ResourceNotFoundException("Hackathon not found");
                });

        hackathonRepository.delete(h);

        log.info("Hackathon deleted permanently with id={}", hackathonId);

        return "Hackathon deleted permanently";
    }

    @Override
    public Page<HackathonDTO> getAllHackathonsPaged(
            int page,
            int size,
            String sortBy,
            String direction) {

        log.info("Fetching paged hackathons: page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction);

        Pageable pageable = PageRequest.of(
                page,
                size,
                direction.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending()
        );

        Page<Hackathon> hackathons = hackathonRepository.findAll(pageable);

        log.info("Fetched {} hackathons in current page", hackathons.getNumberOfElements());

        return hackathons.map(h -> mapper.map(h, HackathonDTO.class));
    }
}
