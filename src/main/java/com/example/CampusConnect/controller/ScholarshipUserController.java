package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.*;
import com.example.CampusConnect.service.ScholarshipUserService;
import com.example.CampusConnect.util.PagedResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/scholarships")
@RequiredArgsConstructor
public class ScholarshipUserController {

    private static final Logger log =
            LoggerFactory.getLogger(ScholarshipUserController.class);

    private final ScholarshipUserService userService;

    @GetMapping("/published")
    public ResponseEntity<PagedResponse<ScholarshipDTO>> published(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "deadline") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("User request: fetch published scholarships");
        log.debug("page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

        return ResponseEntity.ok(
                userService.listPublishedScholarships(page, size, sortBy, sortDir)
        );
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
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadlineBefore,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadlineAfter,
            @RequestParam(required = false) String eligibilityContains
    ) {
        log.info("User request: filter scholarships");
        log.debug(
                "category={}, provider={}, minAmount={}, maxAmount={}, deadlineBefore={}, deadlineAfter={}, eligibilityContains={}",
                category, provider, minAmount, maxAmount, deadlineBefore, deadlineAfter, eligibilityContains
        );

        return ResponseEntity.ok(
                userService.filterScholarships(
                        page, size, sortBy, sortDir,
                        category, provider, minAmount, maxAmount,
                        deadlineBefore, deadlineAfter, eligibilityContains
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScholarshipDTO> getOne(@PathVariable Long id) {
        log.info("User request: fetch scholarship details");
        log.debug("scholarshipId={}", id);

        return ResponseEntity.ok(
                userService.getScholarshipDetails(id)
        );
    }
}
