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

    // Paged list for users
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
        log.debug("Total courses fetched: {}", courses.getTotalElements());

        Page<CourseResponseDTO> dtoPage =
                courses.map(c -> modelMapper.map(c, CourseResponseDTO.class));

        return ResponseEntity.ok(dtoPage);
    }

    // Free courses
    @GetMapping("/free")
    public ResponseEntity<List<CourseResponseDTO>> getFreeCourses() {
        log.info("User request: fetch free courses");

        List<Course> list = userService.getFreeCourses();
        log.debug("Free courses count: {}", list.size());

        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Filter by domain
    @GetMapping("/filter/domain/{domain}")
    public ResponseEntity<List<CourseResponseDTO>> filterByDomain(@PathVariable String domain) {
        log.info("User request: filter courses by domain");
        log.debug("domain={}", domain);

        List<Course> list = userService.filterByDomain(domain);
        log.debug("Courses found: {}", list.size());

        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Filter by technology
    @GetMapping("/filter/technology/{tech}")
    public ResponseEntity<List<CourseResponseDTO>> filterByTechnology(@PathVariable("tech") String tech) {
        log.info("User request: filter courses by technology");
        log.debug("technology={}", tech);

        List<Course> list = userService.filterByTechnology(tech);
        log.debug("Courses found: {}", list.size());

        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Filter by instructor
    @GetMapping("/filter/instructor/{instructor}")
    public ResponseEntity<List<CourseResponseDTO>> filterByInstructor(@PathVariable String instructor) {
        log.info("User request: filter courses by instructor");
        log.debug("instructor={}", instructor);

        List<Course> list = userService.filterByInstructor(instructor);
        log.debug("Courses found: {}", list.size());

        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Filter by course type (enum)
    @GetMapping("/filter/type/{type}")
    public ResponseEntity<List<CourseResponseDTO>> filterByType(@PathVariable CourseType type) {
        log.info("User request: filter courses by type");
        log.debug("courseType={}", type);

        List<Course> list = userService.filterByCourseType(type);
        log.debug("Courses found: {}", list.size());

        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Get single course by id
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        log.info("User request: fetch course by id");
        log.debug("courseId={}", id);

        Course course = userService.getCourseById(id);
        if (course == null) {
            log.warn("Course not found, id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(modelMapper.map(course, CourseResponseDTO.class));
    }

    @PostMapping("/{courseId}/enroll/{userId}")
    public ResponseEntity<CourseEnrollmentDTO> enroll(
            @PathVariable Long courseId,
            @PathVariable Long userId,
            @Valid @RequestBody CourseEnrollmentRequest request) {

        log.info("User request: enroll in course");
        log.debug("userId={}, courseId={}", userId, courseId);

        return ResponseEntity.ok(userService.enroll(userId, courseId, request));
    }

    // Fetch user's enrollments
    @GetMapping("/enrollments/{userId}")
    public ResponseEntity<List<CourseEnrollmentDTO>> myEnrollments(@PathVariable Long userId) {
        log.info("User request: fetch course enrollments");
        log.debug("userId={}", userId);

        return ResponseEntity.ok(userService.myEnrollments(userId));
    }
}
