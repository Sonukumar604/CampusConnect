package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CreateHackathonDTO;
import com.example.CampusConnect.dto.HackathonDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Hackathon;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.HackathonRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.HackathonAdminService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HackathonAdminServiceImpl implements HackathonAdminService {

    @Autowired
    private HackathonRepository hackathonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public HackathonDTO createHackathon(CreateHackathonDTO dto, Long adminId) {

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        Hackathon h = mapper.map(dto, Hackathon.class);
        h.assignCreator(admin);
        h.setRegisteredParticipantsCount(0);
        h.setActive(true);
        h.setRegistrationsOpen(true);

        Hackathon saved = hackathonRepository.save(h);
        return mapper.map(saved, HackathonDTO.class);
    }

    @Override
    public HackathonDTO updateHackathon(Long hackathonId, CreateHackathonDTO dto) {

        Hackathon existing = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));

        mapper.map(dto, existing); // update fields

        Hackathon updated = hackathonRepository.save(existing);
        return mapper.map(updated, HackathonDTO.class);
    }

    @Override
    public List<HackathonDTO> getAllHackathons() {
        return hackathonRepository.findAll()
                .stream()
                .map(h -> mapper.map(h, HackathonDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public HackathonDTO getHackathonById(Long hackathonId) {
        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));
        return mapper.map(h, HackathonDTO.class);
    }

    @Override
    public String toggleHackathonStatus(Long hackathonId) {
        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));

        h.setActive(!h.isActive());
        hackathonRepository.save(h);

        return h.isActive() ? "Hackathon activated" : "Hackathon deactivated";
    }

    @Override
    public String deleteHackathon(Long hackathonId) {
        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found"));

        hackathonRepository.delete(h);
        return "Hackathon deleted permanently";
    }

    @Override
    public Page<HackathonDTO> getAllHackathonsPaged(int page, int size, String sortBy, String direction) {

        Pageable pageable = PageRequest.of(
                page, size,
                direction.equalsIgnoreCase("desc") ?
                        Sort.by(sortBy).descending() :
                        Sort.by(sortBy).ascending()
        );

        Page<Hackathon> hackathons = hackathonRepository.findAll(pageable);

        return hackathons.map(h -> mapper.map(h, HackathonDTO.class));
    }

}

