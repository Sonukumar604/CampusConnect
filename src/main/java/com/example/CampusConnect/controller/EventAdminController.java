package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.CreateEventDTO;
import com.example.CampusConnect.dto.EventDTO;
import com.example.CampusConnect.dto.UpdateEventDTO;
import com.example.CampusConnect.service.EventAdminService;
import com.example.CampusConnect.util.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private static final Logger log =
            LoggerFactory.getLogger(EventAdminController.class);

    private final EventAdminService adminService;

    @PostMapping("/{adminId}")
    public ResponseEntity<EventDTO> create(
            @PathVariable Long adminId,
            @Valid @RequestBody CreateEventDTO dto) {

        log.info("Admin request: create event");
        log.debug("adminId={}", adminId);

        return ResponseEntity.ok(adminService.createEvent(adminId, dto));
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<EventDTO> update(
            @PathVariable Long adminId,
            @Valid @RequestBody UpdateEventDTO dto) {

        log.info("Admin request: update event");
        log.debug("adminId={}", adminId);

        return ResponseEntity.ok(adminService.updateEvent(adminId, dto));
    }

    @DeleteMapping("/{adminId}/{eventId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long adminId,
            @PathVariable Long eventId) {

        log.info("Admin request: delete event");
        log.debug("adminId={}, eventId={}", adminId, eventId);

        adminService.deleteEvent(adminId, eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getOne(@PathVariable Long eventId) {
        log.info("Admin request: fetch event by id");
        log.debug("eventId={}", eventId);

        return ResponseEntity.ok(adminService.getEventById(eventId));
    }

    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<EventDTO>> paged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "startDateTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String type) {

        log.info("Admin request: fetch paged events");
        log.debug("page={}, size={}, sortBy={}, sortDir={}, type={}",
                page, size, sortBy, sortDir, type);

        return ResponseEntity.ok(
                adminService.getEventsPaged(page, size, sortBy, sortDir, type)
        );
    }
}
