package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.*;
import com.example.CampusConnect.service.ScholarshipUserService;
import com.example.CampusConnect.util.PagedResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/scholarships")
@RequiredArgsConstructor
public class ScholarshipUserController {

    private final ScholarshipUserService userService;

    @GetMapping("/published")
    public ResponseEntity<PagedResponse<ScholarshipDTO>> published(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "deadline") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(userService.listPublishedScholarships(page, size, sortBy, sortDir));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponse<ScholarshipDTO>> filter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "deadline") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadlineBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadlineAfter,
            @RequestParam(required = false) String eligibilityContains
    ) {
        return ResponseEntity.ok(userService.filterScholarships(page, size, sortBy, sortDir, category, provider, minAmount, maxAmount, deadlineBefore, deadlineAfter, eligibilityContains));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScholarshipDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getScholarshipDetails(id));
    }
}
