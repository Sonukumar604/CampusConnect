package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.HackathonRegistrationDTO;
import com.example.CampusConnect.dto.RegistrationHackathonRequest;

import java.util.List;

public interface HackathonRegistrationService {
    HackathonRegistrationDTO registerUser(Long userId, Long hackathonId, RegistrationHackathonRequest request);

    List<HackathonRegistrationDTO> getRegistrationsByUser(Long userId);
}
