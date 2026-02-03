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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {

    private static final Logger log =
            LoggerFactory.getLogger(EventAdminServiceImpl.class);

    private final EventRepository eventRepository;
    private final ModelMapper mapper;
    private final com.example.CampusConnect.repository.UserRepository userRepository;

    @Override
    public EventDTO createEvent(Long adminId, CreateEventDTO dto) {
        log.info("Admin {} is creating a new event", adminId);

        com.example.CampusConnect.model.User admin = userRepository.findById(adminId)
                .orElseThrow(() -> {
                    log.error("Event creation failed: Admin not found with id={}", adminId);
                    return new ResourceNotFoundException("Admin not found");
                });

        Event event = mapper.map(dto, Event.class);
        event.setCreatedByUser(admin);

        Event saved = eventRepository.save(event);
        log.info("Event created successfully with id={}", saved.getId());

        return toDto(saved);
    }

    @Override
    public EventDTO updateEvent(Long adminId, UpdateEventDTO dto) {
        log.info("Admin {} is updating event id={}", adminId, dto.getId());

        Event event = eventRepository.findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("Event update failed: Event not found with id={}", dto.getId());
                    return new ResourceNotFoundException("Event not found");
                });

        // Optionally check admin privileges here

        mapper.map(dto, event);
        Event updated = eventRepository.save(event);

        log.info("Event updated successfully with id={}", updated.getId());
        return toDto(updated);
    }

    @Override
    public void deleteEvent(Long adminId, Long eventId) {
        log.info("Admin {} requested deletion of event id={}", adminId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event deletion failed: Event not found with id={}", eventId);
                    return new ResourceNotFoundException("Event not found");
                });

        eventRepository.delete(event);
        log.info("Event deleted successfully with id={}", eventId);
    }

    @Override
    public EventDTO getEventById(Long eventId) {
        log.info("Fetching event details for eventId={}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event not found with id={}", eventId);
                    return new ResourceNotFoundException("Event not found");
                });

        return toDto(event);
    }

    @Override
    public PagedResponse<EventDTO> getEventsPaged(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String filterType) {

        log.info("Fetching paged events: page={}, size={}, sortBy={}, sortDir={}, filterType={}",
                page, size, sortBy, sortDir, filterType);

        Sort sort = Sort.by(sortBy);
        sort = "desc".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Event> p;
        if (filterType == null || filterType.isBlank()) {
            log.debug("No event type filter applied");
            p = eventRepository.findAll(pageable);
        } else {
            try {
                log.debug("Applying event type filter: {}", filterType);
                p = eventRepository.findByEventTypeAndPublishStatus(
                        com.example.CampusConnect.model.EventType.valueOf(filterType),
                        PublishStatus.PUBLISHED,
                        pageable);
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid event type filter '{}', fetching all events", filterType);
                p = eventRepository.findAll(pageable);
            }
        }

        log.info("Fetched {} events (totalElements={})",
                p.getNumberOfElements(), p.getTotalElements());

        return new PagedResponse<>(
                p.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages(),
                p.isLast()
        );
    }

    private EventDTO toDto(Event e) {
        return mapper.map(e, EventDTO.class);
    }
}
