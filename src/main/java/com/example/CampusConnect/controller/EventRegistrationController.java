package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.EventRegistrationDTO;
import com.example.CampusConnect.dto.EventRegistrationRequest;
import com.example.CampusConnect.service.EventRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/events/registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private static final Logger log =
            LoggerFactory.getLogger(EventRegistrationController.class);

    private final EventRegistrationService registrationService;

    @PostMapping("/{eventId}/user/{userId}")
    public ResponseEntity<EventRegistrationDTO> register(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRegistrationRequest request) {

        log.info("User request: register for event");
        log.debug("userId={}, eventId={}", userId, eventId);

        return ResponseEntity.ok(
                registrationService.register(userId, eventId, request)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventRegistrationDTO>> myRegistrations(
            @PathVariable Long userId) {

        log.info("User request: fetch event registrations");
        log.debug("userId={}", userId);

        return ResponseEntity.ok(
                registrationService.myRegistrations(userId)
        );
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventRegistrationDTO>> listForEvent(
            @PathVariable Long eventId) {

        log.info("Admin/Organizer request: fetch registrations for event");
        log.debug("eventId={}", eventId);

        return ResponseEntity.ok(
                registrationService.listRegistrationsForEvent(eventId)
        );
    }

    @DeleteMapping("/{eventId}/user/{userId}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long userId,
            @PathVariable Long eventId) {

        log.warn("User request: cancel event registration");
        log.debug("userId={}, eventId={}", userId, eventId);

        registrationService.cancelRegistration(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}
