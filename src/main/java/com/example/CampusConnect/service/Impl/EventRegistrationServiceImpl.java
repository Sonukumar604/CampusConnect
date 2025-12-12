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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public EventRegistrationDTO register(Long userId, Long eventId, EventRegistrationRequest request) {

        if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new IllegalArgumentException("Already registered for this event");
        }

        var user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Check capacity
        if (event.getRegistrationLimit() != null && event.getRegistrations().size() >= event.getRegistrationLimit()) {
            throw new IllegalStateException("Registration limit reached");
        }

        EventRegistration reg = EventRegistration.builder()
                .event(event)
                .user(user)
                .notes(request != null ? request.getNotes() : null)
                .build();

        var saved = registrationRepository.save(reg);
        return mapper.map(saved, EventRegistrationDTO.class);
    }

    @Override
    public List<EventRegistrationDTO> myRegistrations(Long userId) {
        return registrationRepository.findByUserId(userId).stream().map(r -> mapper.map(r, EventRegistrationDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<EventRegistrationDTO> listRegistrationsForEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId).stream().map(r -> mapper.map(r, EventRegistrationDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void cancelRegistration(Long userId, Long eventId) {
        var maybe = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        registrationRepository.delete(maybe);
    }
}

