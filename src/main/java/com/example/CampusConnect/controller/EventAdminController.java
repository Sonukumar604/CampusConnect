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

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventAdminService adminService;

    @PostMapping("/{adminId}")
    public ResponseEntity<EventDTO> create(@PathVariable Long adminId, @Valid @RequestBody CreateEventDTO dto) {
        return ResponseEntity.ok(adminService.createEvent(adminId, dto));
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<EventDTO> update(@PathVariable Long adminId, @Valid @RequestBody UpdateEventDTO dto) {
        return ResponseEntity.ok(adminService.updateEvent(adminId, dto));
    }

    @DeleteMapping("/{adminId}/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable Long adminId, @PathVariable Long eventId) {
        adminService.deleteEvent(adminId, eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getOne(@PathVariable Long eventId) {
        return ResponseEntity.ok(adminService.getEventById(eventId));
    }

    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<EventDTO>> paged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "startDateTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String type) {

        return ResponseEntity.ok(adminService.getEventsPaged(page, size, sortBy, sortDir, type));
    }
}
