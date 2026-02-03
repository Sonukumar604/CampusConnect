package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.*;
import com.example.CampusConnect.service.ScholarshipAdminService;
import com.example.CampusConnect.util.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin/scholarships")
@RequiredArgsConstructor
public class ScholarshipAdminController {

    private static final Logger log =
            LoggerFactory.getLogger(ScholarshipAdminController.class);

    private final ScholarshipAdminService adminService;

    @PostMapping("/{adminId}")
    public ResponseEntity<ScholarshipDTO> create(
            @PathVariable Long adminId,
            @Valid @RequestBody CreateScholarshipDTO dto) {

        log.info("Admin {} requested to CREATE scholarship", adminId);
        log.debug("CreateScholarshipDTO received: {}", dto);

        ScholarshipDTO response = adminService.createScholarship(adminId, dto);

        log.info("Scholarship created successfully with id {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{adminId}/{scholarshipId}")
    public ResponseEntity<ScholarshipDTO> update(
            @PathVariable Long adminId,
            @PathVariable Long scholarshipId,
            @Valid @RequestBody UpdateScholarshipDTO dto) {

        log.info("Admin {} requested to UPDATE scholarship {}", adminId, scholarshipId);
        log.debug("UpdateScholarshipDTO received: {}", dto);

        ScholarshipDTO response =
                adminService.updateScholarship(adminId, scholarshipId, dto);

        log.info("Scholarship {} updated successfully", scholarshipId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{adminId}/{scholarshipId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long adminId,
            @PathVariable Long scholarshipId) {

        log.info("Admin {} requested to DELETE scholarship {}", adminId, scholarshipId);

        adminService.deleteScholarship(adminId, scholarshipId);

        log.info("Scholarship {} deleted successfully", scholarshipId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{scholarshipId}")
    public ResponseEntity<ScholarshipDTO> getOne(@PathVariable Long scholarshipId) {

        log.info("Fetching scholarship details for id {}", scholarshipId);

        ScholarshipDTO response = adminService.getScholarshipById(scholarshipId);

        log.info("Scholarship {} fetched successfully", scholarshipId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<ScholarshipDTO>> paged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "deadline") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category
    ) {

        log.info("Fetching paged scholarships [page={}, size={}, sortBy={}, sortDir={}, category={}]",
                page, size, sortBy, sortDir, category);

        PagedResponse<ScholarshipDTO> response =
                adminService.getScholarshipsPaged(page, size, sortBy, sortDir, category);

        log.info("Fetched {} scholarships on page {}", response.getContent().size(), page);
        return ResponseEntity.ok(response);
    }
}
