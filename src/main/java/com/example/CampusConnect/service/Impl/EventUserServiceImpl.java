package com.example.CampusConnect.service.Impl;


import com.example.CampusConnect.dto.EventDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Event;
import com.example.CampusConnect.model.PublishStatus;
import com.example.CampusConnect.repository.EventRepository;
import com.example.CampusConnect.service.EventUserService;
import com.example.CampusConnect.util.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventUserServiceImpl implements EventUserService {

    private final EventRepository eventRepository;
    private final ModelMapper mapper;

    @Override
    public PagedResponse<EventDTO> listPublishedEvents(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortBy);
        sort = "desc".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Event> p = eventRepository.findByPublishStatus(PublishStatus.PUBLISHED, pageable);
        return new PagedResponse<>(p.getContent().stream().map(this::toDto).collect(Collectors.toList()), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.isLast());
    }

    @Override
    public List<EventDTO> upcomingEvents(int limit) {
        LocalDateTime now = LocalDateTime.now();
        Page<Event> p = eventRepository.findByStartDateTimeBetweenAndPublishStatus(now, now.plusYears(1), PublishStatus.PUBLISHED, PageRequest.of(0, limit, Sort.by("startDateTime")));
        return p.getContent().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public PagedResponse<EventDTO> filterEvents(int page, int size, String sortBy, String sortDir, String type, String mode, String location, LocalDateTime from, LocalDateTime to) {
        Sort sort = Sort.by(sortBy);
        sort = "desc".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Simple filter implementation (expandable to Specification/Criteria)
        Page<Event> p = eventRepository.findByPublishStatus(PublishStatus.PUBLISHED, pageable);
        List<Event> filtered = p.getContent().stream()
                .filter(e -> {
                    boolean ok = true;
                    if (type != null && !type.isBlank()) {
                        try { ok &= e.getEventType() == com.example.CampusConnect.model.EventType.valueOf(type); } catch (Exception ex) { ok &= true; }
                    }
                    if (mode != null && !mode.isBlank()) {
                        try { ok &= e.getMode() == com.example.CampusConnect.model.EventMode.valueOf(mode); } catch (Exception ex) { ok &= true; }
                    }
                    if (location != null && !location.isBlank()) {
                        ok &= e.getLocation() != null && e.getLocation().toLowerCase().contains(location.toLowerCase());
                    }
                    if (from != null) ok &= e.getStartDateTime() != null && !e.getStartDateTime().isBefore(from);
                    if (to != null) ok &= e.getEndDateTime() != null && !e.getEndDateTime().isAfter(to);
                    return ok;
                }).collect(Collectors.toList());

        return new PagedResponse<>(filtered.stream().map(this::toDto).collect(Collectors.toList()), page, size, filtered.size(), 1, true);
    }

    @Override
    public EventDTO getEventDetails(Long eventId) {
        return toDto(eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found")));
    }

    private EventDTO toDto(Event e) {
        EventDTO dto = mapper.map(e, EventDTO.class);

        return dto;
    }
}
