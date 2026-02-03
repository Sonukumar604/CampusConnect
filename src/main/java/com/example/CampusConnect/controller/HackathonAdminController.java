package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.CreateHackathonDTO;
import com.example.CampusConnect.dto.HackathonDTO;
import com.example.CampusConnect.service.HackathonAdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/admin/hackathons")
public class HackathonAdminController {

    private static final Logger log =
            LoggerFactory.getLogger(HackathonAdminController.class);

    @Autowired
    private HackathonAdminService adminService;

    @PostMapping("/{adminId}")
    public ResponseEntity<HackathonDTO> createHackathon(
            @PathVariable Long adminId,
            @Valid @RequestBody CreateHackathonDTO dto
    ) {
        log.info("Admin request: create hackathon");
        log.debug("adminId={}", adminId);

        return new ResponseEntity<>(
                adminService.createHackathon(dto, adminId),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{hackathonId}")
    public ResponseEntity<HackathonDTO> updateHackathon(
            @PathVariable Long hackathonId,
            @Valid @RequestBody CreateHackathonDTO dto
    ) {
        log.info("Admin request: update hackathon");
        log.debug("hackathonId={}", hackathonId);

        return ResponseEntity.ok(
                adminService.updateHackathon(hackathonId, dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<HackathonDTO>> getAll() {
        log.info("Admin request: fetch all hackathons");
        return ResponseEntity.ok(adminService.getAllHackathons());
    }

    @GetMapping("/{hackathonId}")
    public ResponseEntity<HackathonDTO> getOne(@PathVariable Long hackathonId) {
        log.info("Admin request: fetch hackathon by id");
        log.debug("hackathonId={}", hackathonId);

        return ResponseEntity.ok(
                adminService.getHackathonById(hackathonId)
        );
    }

    @PutMapping("/{hackathonId}/toggle")
    public ResponseEntity<String> toggleStatus(@PathVariable Long hackathonId) {
        log.warn("Admin request: toggle hackathon status");
        log.debug("hackathonId={}", hackathonId);

        return ResponseEntity.ok(
                adminService.toggleHackathonStatus(hackathonId)
        );
    }

    @DeleteMapping("/{hackathonId}")
    public ResponseEntity<String> delete(@PathVariable Long hackathonId) {
        log.warn("Admin request: delete hackathon");
        log.debug("hackathonId={}", hackathonId);

        return ResponseEntity.ok(
                adminService.deleteHackathon(hackathonId)
        );
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<HackathonDTO>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Admin request: fetch paged hackathons");
        log.debug("page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction);

        return ResponseEntity.ok(
                adminService.getAllHackathonsPaged(page, size, sortBy, direction)
        );
    }
}
