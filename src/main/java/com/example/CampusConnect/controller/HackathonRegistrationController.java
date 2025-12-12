package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.HackathonRegistrationDTO;
import com.example.CampusConnect.dto.RegistrationHackathonRequest;
import com.example.CampusConnect.service.HackathonRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hackathons/registrations")
public class HackathonRegistrationController {

    @Autowired
    @Qualifier("hackathonRegistrationServiceImpl")
    private HackathonRegistrationService registrationService;

    @PostMapping("/{hackathonId}/user/{userId}")
    public ResponseEntity<HackathonRegistrationDTO> register(
            @PathVariable Long hackathonId,
            @PathVariable Long userId,
            @Valid @RequestBody RegistrationHackathonRequest request
    ) {
        return new ResponseEntity<>(registrationService.registerUser(userId, hackathonId, request), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HackathonRegistrationDTO>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByUser(userId));
    }
}

