package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.EventDTO;
import com.example.CampusConnect.service.EventUserService;
import com.example.CampusConnect.util.PagedResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user/events")
@RequiredArgsConstructor
public class EventUserController {

    private static final Logger log =
            LoggerFactory.getLogger(EventUserController.class);

    private final EventUserService userService;

    @GetMapping("/published")
    public ResponseEntity<PagedResponse<EventDTO>> published(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "startDateTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("User request: fetch published events");
        log.debug("page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

        return ResponseEntity.ok(
                userService.listPublishedEvents(page, size, sortBy, sortDir)
        );
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDTO>> upcoming(
            @RequestParam(defaultValue = "5") @Min(1) int limit) {

        log.info("User request: fetch upcoming events");
        log.debug("limit={}", limit);

        return ResponseEntity.ok(
                userService.upcomingEvents(limit)
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<EventDTO>> filter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "startDateTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String location,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        log.info("User request: filter events");
        log.debug(
                "page={}, size={}, sortBy={}, sortDir={}, type={}, mode={}, location={}, from={}, to={}",
                page, size, sortBy, sortDir, type, mode, location, from, to
        );

        return ResponseEntity.ok(
                userService.filterEvents(
                        page, size, sortBy, sortDir, type, mode, location, from, to
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getOne(@PathVariable Long id) {
        log.info("User request: fetch event details");
        log.debug("eventId={}", id);

        return ResponseEntity.ok(
                userService.getEventDetails(id)
        );
    }
}
