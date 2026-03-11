package com.example.CampusConnect.controller;

import com.example.CampusConnect.service.ScholarshipApplicationAdminService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin/scholarship-applications")
@RequiredArgsConstructor
public class ScholarshipApplicationAdminController {

    private static final Logger log =
            LoggerFactory.getLogger(ScholarshipApplicationAdminController.class);

    private final ScholarshipApplicationAdminService service;

    // ================= FETCH APPLICATIONS =================

    @PreAuthorize("hasAuthority('SCHOLARSHIP_APPLICATION_VIEW')")
    @GetMapping("/{scholarshipId}")
    public ResponseEntity<?> getApplications(@PathVariable Long scholarshipId) {

        log.info("Admin request: fetch scholarship applications");
        log.debug("scholarshipId={}", scholarshipId);

        return ResponseEntity.ok(
                service.getApplicationsForScholarship(scholarshipId)
        );
    }

    // ================= UPDATE APPLICATION STATUS =================

    @PreAuthorize("hasAuthority('SCHOLARSHIP_APPLICATION_UPDATE')")
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long applicationId,
            @RequestParam @Valid String status
    ) {

        log.info("Admin request: update scholarship application status");
        log.debug("applicationId={}, status={}", applicationId, status);

        return ResponseEntity.ok(
                service.updateStatus(applicationId, status)
        );
    }
}