package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.CourseEnrollmentDTO;
import com.example.CampusConnect.dto.CourseEnrollmentRequest;
import com.example.CampusConnect.dto.CourseResponseDTO;
import com.example.CampusConnect.model.Course;
import com.example.CampusConnect.model.CourseType;
import com.example.CampusConnect.service.CourseUserService;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/courses")
public class CourseUserController {

    private static final Logger log =
            LoggerFactory.getLogger(CourseUserController.class);

    @Autowired
    private CourseUserService userService;

    @Autowired
    private ModelMapper modelMapper;

    // ================= PAGED COURSES =================

    @PreAuthorize("hasAuthority('COURSE_ENROLL')")
    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> getCoursesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        log.info("User request: fetch paged courses");
        log.debug("page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

        Page<Course> courses = userService.getAllCoursesPaged(page, size, sortBy, sortDir);

        Page<CourseResponseDTO> dtoPage =
                courses.map(c -> modelMapper.map(c, CourseResponseDTO.class));

        return ResponseEntity.ok(dtoPage);
    }

    // ================= FREE COURSES =================

    @PreAuthorize("hasAuthority('COURSE_ENROLL')")
    @GetMapping("/free")
    public ResponseEntity<List<CourseResponseDTO>> getFreeCourses() {

        log.info("User request: fetch free courses");

        List<Course> list = userService.getFreeCourses();

        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ================= FILTER DOMAIN =================

    @PreAuthorize("hasAuthority('COURSE_ENROLL')")
    @GetMapping("/filter/domain/{domain}")
    public ResponseEntity<List<CourseResponseDTO>> filterByDomain(@PathVariable String domain) {

        log.info("User request: filter courses by domain {}", domain);

        List<Course> list = userService.filterByDomain(domain);

        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ================= FILTER TECHNOLOGY =================

    @PreAuthorize("hasAuthority('COURSE_ENROLL')")
    @GetMapping("/filter/technology/{tech}")
    public ResponseEntity<List<CourseResponseDTO>> filterByTechnology(@PathVariable("tech") String tech) {

        log.info("User request: filter courses by technology {}", tech);

        List<Course> list = userService.filterByTechnology(tech);

        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ================= FILTER INSTRUCTOR =================

    @PreAuthorize("hasAuthority('COURSE_ENROLL')")
    @GetMapping("/filter/instructor/{instructor}")
    public ResponseEntity<List<CourseResponseDTO>> filterByInstructor(@PathVariable String instructor) {

        log.info("User request: filter courses by instructor {}", instructor);

        List<Course> list = userService.filterByInstructor(instructor);

        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ================= FILTER TYPE =================

    @PreAuthorize("hasAuthority('COURSE_ENROLL')")
    @GetMapping("/filter/type/{type}")
    public ResponseEntity<List<CourseResponseDTO>> filterByType(@PathVariable CourseType type) {

        log.info("User request: filter courses by type {}", type);

        List<Course> list = userService.filterByCourseType(type);

        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ================= GET COURSE =================

    @PreAuthorize("hasAuthority('COURSE_ENROLL')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {

        log.info("User request: fetch course by id {}", id);

        Course course = userService.getCourseById(id);

        if (course == null) {
            log.warn("Course not found, id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                modelMapper.map(course, CourseResponseDTO.class)
        );
    }

    // ================= ENROLL COURSE =================

    @PreAuthorize("hasAuthority('COURSE_ENROLL')")
    @PostMapping("/{courseId}/enroll/{userId}")
    public ResponseEntity<CourseEnrollmentDTO> enroll(
            @PathVariable Long courseId,
            @PathVariable Long userId,
            @Valid @RequestBody CourseEnrollmentRequest request) {

        log.info("User request: enroll in course userId={} courseId={}", userId, courseId);

        return ResponseEntity.ok(
                userService.enroll(userId, courseId, request)
        );
    }

    // ================= USER ENROLLMENTS =================

    @PreAuthorize("hasAuthority('COURSE_ENROLL')")
    @GetMapping("/enrollments/{userId}")
    public ResponseEntity<List<CourseEnrollmentDTO>> myEnrollments(@PathVariable Long userId) {

        log.info("User request: fetch course enrollments userId={}", userId);

        return ResponseEntity.ok(
                userService.myEnrollments(userId)
        );
    }
}