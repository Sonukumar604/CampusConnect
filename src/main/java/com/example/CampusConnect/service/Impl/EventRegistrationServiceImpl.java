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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {

    private static final Logger log =
            LoggerFactory.getLogger(EventRegistrationServiceImpl.class);

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional // 🔒 Atomic operation
    public EventRegistrationDTO register(Long userId, Long eventId, EventRegistrationRequest request) {

        log.info("User {} attempting to register for event {}", userId, eventId);

        if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            log.warn("Registration failed: user {} already registered for event {}", userId, eventId);
            throw new IllegalArgumentException("Already registered for this event");
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Registration failed: user not found with id={}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Registration failed: event not found with id={}", eventId);
                    return new ResourceNotFoundException("Event not found");
                });

        if (event.getRegistrationLimit() != null &&
                event.getRegistrations().size() >= event.getRegistrationLimit()) {

            log.warn("Registration failed: event {} registration limit reached", eventId);
            throw new IllegalStateException("Registration limit reached");
        }

        EventRegistration reg = EventRegistration.builder()
                .event(event)
                .user(user)
                .notes(request != null ? request.getNotes() : null)
                .build();

        EventRegistration saved = registrationRepository.save(reg);

        log.info("User {} successfully registered for event {}", userId, eventId);

        return mapper.map(saved, EventRegistrationDTO.class);
    }

    @Override
    public List<EventRegistrationDTO> myRegistrations(Long userId) {
        log.info("Fetching registrations for userId={}", userId);

        List<EventRegistrationDTO> result =
                registrationRepository.findByUserId(userId)
                        .stream()
                        .map(r -> mapper.map(r, EventRegistrationDTO.class))
                        .collect(Collectors.toList());

        log.info("Found {} registrations for userId={}", result.size(), userId);
        return result;
    }

    @Override
    public List<EventRegistrationDTO> listRegistrationsForEvent(Long eventId) {
        log.info("Fetching registrations for eventId={}", eventId);

        List<EventRegistrationDTO> result =
                registrationRepository.findByEventId(eventId)
                        .stream()
                        .map(r -> mapper.map(r, EventRegistrationDTO.class))
                        .collect(Collectors.toList());

        log.info("Found {} registrations for eventId={}", result.size(), eventId);
        return result;
    }

    @Override
    public void cancelRegistration(Long userId, Long eventId) {
        log.info("User {} attempting to cancel registration for event {}", userId, eventId);

        var maybe = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> {
                    log.error("Cancel failed: registration not found for user {} and event {}",
                            userId, eventId);
                    return new ResourceNotFoundException("Registration not found");
                });

        registrationRepository.delete(maybe);
        log.info("Registration cancelled successfully for user {} and event {}", userId, eventId);
    }
}
