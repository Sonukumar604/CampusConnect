package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.HackathonDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface HackathonUserService {

    List<HackathonDTO> getAllHackathons();

    List<HackathonDTO> filterHackathons(String technology, String organization, LocalDate startDate);

    HackathonDTO getHackathonById(Long hackathonId);

    Page<HackathonDTO> getAllHackathonsPaged(int page, int size, String sortBy, String direction);

    Page<HackathonDTO> filterHackathonsPaged(
            String technology, String organization, LocalDate startDate,
            int page, int size, String sortBy, String direction
    );

}

