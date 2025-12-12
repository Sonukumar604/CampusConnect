package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.CreateHackathonDTO;
import com.example.CampusConnect.dto.HackathonDTO;
import org.springframework.data.domain.Page;

import java.util.List;


public interface HackathonAdminService {

    HackathonDTO createHackathon(CreateHackathonDTO dto, Long adminId);

    HackathonDTO updateHackathon(Long hackathonId, CreateHackathonDTO dto);

    List<HackathonDTO> getAllHackathons();

    HackathonDTO getHackathonById(Long hackathonId);

    String toggleHackathonStatus(Long hackathonId);

    String deleteHackathon(Long hackathonId);

    Page<HackathonDTO> getAllHackathonsPaged(int page, int size, String sortBy, String direction);

}

