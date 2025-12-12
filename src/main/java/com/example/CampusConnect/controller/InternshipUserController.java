package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.InternshipDTO;
import com.example.CampusConnect.model.InternshipType;
import com.example.CampusConnect.service.InternshipUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internships")
@RequiredArgsConstructor
public class InternshipUserController {

    private final InternshipUserService internshipUserService;

    // ✅ Get all internships (with pagination + sorting)
    @GetMapping
    public Page<InternshipDTO> getAllInternships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postedOn") String sortBy
    ) {
        return internshipUserService.getAllInternships(page, size, sortBy);
    }

    // ✅ Filter by location
    @GetMapping("/location")
    public Page<InternshipDTO> filterByLocation(
            @RequestParam String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return internshipUserService.filterByLocation(location, page, size);
    }

    // ✅ Filter by internship type
    @GetMapping("/type")
    public Page<InternshipDTO> filterByType(
            @RequestParam InternshipType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return internshipUserService.filterByType(type, page, size);
    }

    // ✅ Search internships by company or role
    @GetMapping("/search")
    public Page<InternshipDTO> searchInternships(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return internshipUserService.search(keyword, page, size);
    }
}
