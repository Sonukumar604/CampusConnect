
package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.AdminAnalyticsDTO;
import com.example.CampusConnect.repository.*;
        import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;
    private final InternshipRepository internshipRepository;
    private final CourseRepository courseRepository;

    @GetMapping("/analytics")
    public AdminAnalyticsDTO getAnalytics() {

        return AdminAnalyticsDTO.builder()
                .totalUsers(userRepository.count())
                .totalHackathons(hackathonRepository.count())
                .totalInternships(internshipRepository.count())
                .totalCourses(courseRepository.count())
                .build();
    }
}