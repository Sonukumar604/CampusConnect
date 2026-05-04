package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.EventDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.*;
import com.example.CampusConnect.repository.EventRepository;
import com.example.CampusConnect.service.EventUserService;
import com.example.CampusConnect.util.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")         // 🔐 USER-only access
@Transactional(readOnly = true)          // 📖 Entire service is read-only
public class EventUserServiceImpl implements EventUserService {

    private static final Logger log =
            LoggerFactory.getLogger(EventUserServiceImpl.class);

    private final EventRepository eventRepository;
    private final ModelMapper mapper;

    /* ============================================================
       LIST PUBLISHED EVENTS
       ============================================================ */

    @Override
    public PagedResponse<EventDTO> listPublishedEvents(
            int page,
            int size,
            String sortBy,
            String sortDir) {

        log.info("Listing published events | page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Event> result =
                eventRepository.findByPublishStatus(PublishStatus.PUBLISHED, pageable);

        return buildPagedResponse(result);
    }

    /* ============================================================
       UPCOMING EVENTS
       ============================================================ */

    @Override
    public List<EventDTO> upcomingEvents(int limit) {

        log.info("Fetching upcoming events | limit={}", limit);

        LocalDateTime now = LocalDateTime.now();

        Page<Event> result =
                eventRepository.findByStartDateTimeBetweenAndPublishStatus(
                        now,
                        now.plusYears(1),
                        PublishStatus.PUBLISHED,
                        PageRequest.of(0, limit, Sort.by("startDateTime"))
                );

        return result.getContent()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* ============================================================
       FILTER EVENTS
       ============================================================ */

    @Override
    public PagedResponse<EventDTO> filterEvents(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String type,
            String mode,
            String location,
            LocalDateTime from,
            LocalDateTime to) {

        log.info("Filtering events | page={}, size={}, type={}, mode={}, location={}",
                page, size, type, mode, location);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Event> pageResult =
                eventRepository.findByPublishStatus(PublishStatus.PUBLISHED, pageable);

        List<Event> filtered = pageResult.getContent().stream()
                .filter(event -> matchesFilters(event, type, mode, location, from, to))
                .collect(Collectors.toList());

        return new PagedResponse<>(
                filtered.stream().map(this::toDto).collect(Collectors.toList()),
                page,
                size,
                filtered.size(),
                1,
                true
        );
    }

    /* ============================================================
       GET EVENT DETAILS
       ============================================================ */

    @Override
    public EventDTO getEventDetails(Long eventId) {

        log.info("Fetching event details | eventId={}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Event not found"));

        return toDto(event);
    }

    /* ============================================================
       HELPER METHODS
       ============================================================ */

    private boolean matchesFilters(Event e,
                                   String type,
                                   String mode,
                                   String location,
                                   LocalDateTime from,
                                   LocalDateTime to) {

        boolean ok = true;

        if (type != null && !type.isBlank()) {
            try {
                ok &= e.getEventType() == EventType.valueOf(type);
            } catch (Exception ignored) {
                log.debug("Invalid event type ignored: {}", type);
            }
        }

        if (mode != null && !mode.isBlank()) {
            try {
                ok &= e.getMode() == EventMode.valueOf(mode);
            } catch (Exception ignored) {
                log.debug("Invalid event mode ignored: {}", mode);
            }
        }

        if (location != null && !location.isBlank()) {
            ok &= e.getLocation() != null &&
                    e.getLocation().toLowerCase().contains(location.toLowerCase());
        }

        if (from != null) {
            ok &= e.getStartDateTime() != null &&
                    !e.getStartDateTime().isBefore(from);
        }

        if (to != null) {
            ok &= e.getEndDateTime() != null &&
                    !e.getEndDateTime().isAfter(to);
        }

        return ok;
    }

    private PagedResponse<EventDTO> buildPagedResponse(Page<Event> page) {

        return new PagedResponse<>(
                page.getContent().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    private EventDTO toDto(Event event) {
        return mapper.map(event, EventDTO.class);
    }
}

