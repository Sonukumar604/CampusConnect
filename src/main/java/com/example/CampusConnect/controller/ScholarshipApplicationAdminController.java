
package com.example.CampusConnect.controller;

import com.example.CampusConnect.service.ScholarshipApplicationAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/scholarship-applications")
@RequiredArgsConstructor
public class ScholarshipApplicationAdminController {

    private final ScholarshipApplicationAdminService service;

    @GetMapping("/{scholarshipId}")
    public ResponseEntity<?> getApplications(@PathVariable Long scholarshipId) {
        return ResponseEntity.ok(service.getApplicationsForScholarship(scholarshipId));
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long applicationId,
            @RequestParam@Valid String status
    ) {
        return ResponseEntity.ok(service.updateStatus(applicationId, status));
    }
}
