package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.EventRegistrationDTO;
import com.example.CampusConnect.dto.EventRegistrationRequest;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Event;
import com.example.CampusConnect.model.EventRegistration;
import com.example.CampusConnect.repository.EventRegistrationRepository;
import com.example.CampusConnect.repository.EventRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.EventRegistrationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")   // Only USER can register/cancel
@Transactional                     // Write-heavy service
public class EventRegistrationServiceImpl implements EventRegistrationService {

    private static final Logger log =
            LoggerFactory.getLogger(EventRegistrationServiceImpl.class);

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    /* ============================================================
       REGISTER
       ============================================================ */

    @Override
    public EventRegistrationDTO register(
            Long userId,
            Long eventId,
            EventRegistrationRequest request) {

        log.info("User {} attempting to register for event {}", userId, eventId);

        // Prevent duplicate registration
        if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new IllegalArgumentException("Already registered for this event");
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Check registration limit
        if (event.getRegistrationLimit() != null &&
                event.getRegistrations().size() >= event.getRegistrationLimit()) {
            throw new IllegalStateException("Registration limit reached");
        }

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .user(user)
                .notes(request != null ? request.getNotes() : null)
                .build();

        EventRegistration saved = registrationRepository.save(registration);

        log.info("User {} successfully registered for event {}", userId, eventId);

        return mapper.map(saved, EventRegistrationDTO.class);
    }

    /* ============================================================
       MY REGISTRATIONS
       ============================================================ */

    @Override
    @Transactional(readOnly = true)
    public List<EventRegistrationDTO> myRegistrations(Long userId) {

        log.info("Fetching registrations for userId={}", userId);

        return registrationRepository.findByUserId(userId)
                .stream()
                .map(r -> mapper.map(r, EventRegistrationDTO.class))
                .collect(Collectors.toList());
    }

    /* ============================================================
       LIST REGISTRATIONS FOR EVENT
       ============================================================ */

    @Override
    @Transactional(readOnly = true)
    public List<EventRegistrationDTO> listRegistrationsForEvent(Long eventId) {

        log.info("Fetching registrations for eventId={}", eventId);

        return registrationRepository.findByEventId(eventId)
                .stream()
                .map(r -> mapper.map(r, EventRegistrationDTO.class))
                .collect(Collectors.toList());
    }

    /* ============================================================
       CANCEL
       ============================================================ */

    @Override
    public void cancelRegistration(Long userId, Long eventId) {

        log.info("User {} attempting to cancel registration for event {}", userId, eventId);

        var registration = registrationRepository
                .findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Registration not found"));

        registrationRepository.delete(registration);

        log.info("Registration cancelled for user {} and event {}", userId, eventId);
    }
}
