package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.HackathonDTO;
import com.example.CampusConnect.service.HackathonUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonUserController {

    @Autowired
    private HackathonUserService userService;

    @GetMapping
    public ResponseEntity<List<HackathonDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllHackathons());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<HackathonDTO>> filter(
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate
    ) {
        return ResponseEntity.ok(userService.filterHackathons(technology, organization, startDate));
    }

    @GetMapping("/{hackathonId}")
    public ResponseEntity<HackathonDTO> details(@PathVariable Long hackathonId) {
        return ResponseEntity.ok(userService.getHackathonById(hackathonId));
    }
    @GetMapping("/paged")
    public ResponseEntity<Page<HackathonDTO>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(userService.getAllHackathonsPaged(page, size, sortBy, direction));
    }
    @GetMapping("/filter/paged")
    public ResponseEntity<Page<HackathonDTO>> filterPaged(
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(
                userService.filterHackathonsPaged(technology, organization, startDate, page, size, sortBy, direction)
        );
    }


}
