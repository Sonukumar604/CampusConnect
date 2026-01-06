
package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.CreateScholarshipApplicationRequest;
import com.example.CampusConnect.service.ScholarshipApplicationUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/scholarship-applications")
@RequiredArgsConstructor
public class ScholarshipApplicationUserController {

    private final ScholarshipApplicationUserService service;

    @PostMapping("/{userId}/apply")
    public ResponseEntity<?> apply(
            @PathVariable Long userId,
            @Valid @RequestBody CreateScholarshipApplicationRequest request
    ) {
        return ResponseEntity.ok(service.apply(userId, request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> myApplications(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getMyApplications(userId));
    }
}
