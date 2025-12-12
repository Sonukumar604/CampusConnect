package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.EventRegistrationDTO;
import com.example.CampusConnect.dto.EventRegistrationRequest;
import com.example.CampusConnect.service.EventRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService registrationService;

    @PostMapping("/{eventId}/user/{userId}")
    public ResponseEntity<EventRegistrationDTO> register(@PathVariable Long userId, @PathVariable Long eventId, @Valid @RequestBody EventRegistrationRequest request) {
        return ResponseEntity.ok(registrationService.register(userId, eventId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventRegistrationDTO>> myRegistrations(@PathVariable Long userId) {
        return ResponseEntity.ok(registrationService.myRegistrations(userId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventRegistrationDTO>> listForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.listRegistrationsForEvent(eventId));
    }

    @DeleteMapping("/{eventId}/user/{userId}")
    public ResponseEntity<Void> cancel(@PathVariable Long userId, @PathVariable Long eventId) {
        registrationService.cancelRegistration(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}
