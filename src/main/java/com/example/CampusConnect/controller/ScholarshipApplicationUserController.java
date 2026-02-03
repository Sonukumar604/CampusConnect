package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.CreateScholarshipApplicationRequest;
import com.example.CampusConnect.service.ScholarshipApplicationUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user/scholarship-applications")
@RequiredArgsConstructor
public class ScholarshipApplicationUserController {

    private static final Logger log =
            LoggerFactory.getLogger(ScholarshipApplicationUserController.class);

    private final ScholarshipApplicationUserService service;

    @PostMapping("/{userId}/apply")
    public ResponseEntity<?> apply(
            @PathVariable Long userId,
            @Valid @RequestBody CreateScholarshipApplicationRequest request
    ) {
        log.info("User request: apply for scholarship");
        log.debug("userId={}", userId);

        return ResponseEntity.ok(
                service.apply(userId, request)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> myApplications(@PathVariable Long userId) {
        log.info("User request: fetch scholarship applications");
        log.debug("userId={}", userId);

        return ResponseEntity.ok(
                service.getMyApplications(userId)
        );
    }
}
