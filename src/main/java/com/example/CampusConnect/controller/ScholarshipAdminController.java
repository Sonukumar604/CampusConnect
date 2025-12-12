package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.*;
import com.example.CampusConnect.service.ScholarshipAdminService;
import com.example.CampusConnect.util.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/scholarships")
@RequiredArgsConstructor
public class ScholarshipAdminController {

    private final ScholarshipAdminService adminService;

    @PostMapping("/{adminId}")
    public ResponseEntity<ScholarshipDTO> create(@PathVariable Long adminId, @Valid @RequestBody CreateScholarshipDTO dto) {
        return ResponseEntity.ok(adminService.createScholarship(adminId, dto));
    }

    @PutMapping("/{adminId}/{scholarshipId}")
    public ResponseEntity<ScholarshipDTO> update(@PathVariable Long adminId, @PathVariable Long scholarshipId, @Valid @RequestBody UpdateScholarshipDTO dto) {
        return ResponseEntity.ok(adminService.updateScholarship(adminId, scholarshipId, dto));
    }

    @DeleteMapping("/{adminId}/{scholarshipId}")
    public ResponseEntity<Void> delete(@PathVariable Long adminId, @PathVariable Long scholarshipId) {
        adminService.deleteScholarship(adminId, scholarshipId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{scholarshipId}")
    public ResponseEntity<ScholarshipDTO> getOne(@PathVariable Long scholarshipId) {
        return ResponseEntity.ok(adminService.getScholarshipById(scholarshipId));
    }

    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<ScholarshipDTO>> paged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "deadline") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(adminService.getScholarshipsPaged(page, size, sortBy, sortDir, category));
    }
}
