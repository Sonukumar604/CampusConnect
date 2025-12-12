package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CreateEventDTO;
import com.example.CampusConnect.dto.EventDTO;
import com.example.CampusConnect.dto.UpdateEventDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Event;
import com.example.CampusConnect.model.PublishStatus;
import com.example.CampusConnect.repository.EventRepository;
import com.example.CampusConnect.service.EventAdminService;
import com.example.CampusConnect.util.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final ModelMapper mapper;
    private final com.example.CampusConnect.repository.UserRepository userRepository;

    @Override
    public EventDTO createEvent(Long adminId, CreateEventDTO dto) {
        com.example.CampusConnect.model.User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        Event event = mapper.map(dto, Event.class);
        event.setCreatedBy(admin);
        Event saved = eventRepository.save(event);
        return toDto(saved);
    }

    @Override
    public EventDTO updateEvent(Long adminId, UpdateEventDTO dto) {
        Event event = eventRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Optionally check admin privileges here

        mapper.map(dto, event);
        Event updated = eventRepository.save(event);
        return toDto(updated);
    }

    @Override
    public void deleteEvent(Long adminId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        eventRepository.delete(event);
    }

    @Override
    public EventDTO getEventById(Long eventId) {
        return toDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found")));
    }

    @Override
    public PagedResponse<EventDTO> getEventsPaged(int page, int size, String sortBy, String sortDir, String filterType) {
        Sort sort = Sort.by(sortBy);
        sort = "desc".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Event> p;
        if (filterType == null || filterType.isBlank()) {
            p = eventRepository.findAll(pageable);
        } else {
            try {
                p = eventRepository.findByEventTypeAndPublishStatus(
                        com.example.CampusConnect.model.EventType.valueOf(filterType),
                        PublishStatus.PUBLISHED,
                        pageable);
            } catch (IllegalArgumentException ex) {
                p = eventRepository.findAll(pageable);
            }
        }

        return new PagedResponse<>(
                p.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.isLast());
    }

    private EventDTO toDto(Event e) {
        EventDTO dto = mapper.map(e, EventDTO.class);
        return dto;
    }
}
