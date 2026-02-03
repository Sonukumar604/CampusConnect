package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.HackathonDTO;
import com.example.CampusConnect.service.HackathonUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonUserController {

    private static final Logger log =
            LoggerFactory.getLogger(HackathonUserController.class);

    @Autowired
    private HackathonUserService userService;

    @GetMapping
    public ResponseEntity<List<HackathonDTO>> getAll() {
        log.info("User request: fetch all hackathons");

        return ResponseEntity.ok(
                userService.getAllHackathons()
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<List<HackathonDTO>> filter(
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate
    ) {
        log.info("User request: filter hackathons");
        log.debug("technology={}, organization={}, startDate={}",
                technology, organization, startDate);

        return ResponseEntity.ok(
                userService.filterHackathons(technology, organization, startDate)
        );
    }

    @GetMapping("/{hackathonId}")
    public ResponseEntity<HackathonDTO> details(@PathVariable Long hackathonId) {
        log.info("User request: fetch hackathon details");
        log.debug("hackathonId={}", hackathonId);

        return ResponseEntity.ok(
                userService.getHackathonById(hackathonId)
        );
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<HackathonDTO>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("User request: fetch paged hackathons");
        log.debug("page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction);

        return ResponseEntity.ok(
                userService.getAllHackathonsPaged(page, size, sortBy, direction)
        );
    }

    @GetMapping("/filter/paged")
    public ResponseEntity<Page<HackathonDTO>> filterPaged(
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("User request: filter paged hackathons");
        log.debug(
                "technology={}, organization={}, startDate={}, page={}, size={}, sortBy={}, direction={}",
                technology, organization, startDate, page, size, sortBy, direction
        );

        return ResponseEntity.ok(
                userService.filterHackathonsPaged(
                        technology, organization, startDate, page, size, sortBy, direction
                )
        );
    }
}
