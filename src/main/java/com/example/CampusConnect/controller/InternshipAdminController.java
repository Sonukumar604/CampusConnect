package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.CreateInternshipDTO;
import com.example.CampusConnect.dto.InternshipDTO;
import com.example.CampusConnect.service.InternshipAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin/internships")
@RequiredArgsConstructor
public class InternshipAdminController {

    private static final Logger log =
            LoggerFactory.getLogger(InternshipAdminController.class);

    private final InternshipAdminService internshipAdminService;

    // ✅ Create a new internship
    @PostMapping
    public InternshipDTO createInternship(@Valid @RequestBody CreateInternshipDTO dto) {
        log.info("Admin request: create internship");

        return internshipAdminService.createInternship(dto);
    }

    // ✅ Update an existing internship
    @PutMapping("/{id}")
    public InternshipDTO updateInternship(
            @PathVariable Long id,
            @Valid @RequestBody CreateInternshipDTO dto) {

        log.info("Admin request: update internship");
        log.debug("internshipId={}", id);

        return internshipAdminService.updateInternship(id, dto);
    }

    // ✅ Delete an internship
    @DeleteMapping("/{id}")
    public String deleteInternship(@PathVariable Long id) {
        log.warn("Admin request: delete internship");
        log.debug("internshipId={}", id);

        internshipAdminService.deleteInternship(id);
        return "Internship with ID " + id + " has been deleted successfully.";
    }
}
