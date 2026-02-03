package com.example.CampusConnect.controller;

import com.example.CampusConnect.dto.CourseRequestDTO;
import com.example.CampusConnect.dto.CourseResponseDTO;
import com.example.CampusConnect.model.Course;
import com.example.CampusConnect.service.CourseAdminService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin/courses")
public class CourseAdminController {

    private static final Logger log =
            LoggerFactory.getLogger(CourseAdminController.class);

    @Autowired
    private CourseAdminService adminService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<CourseResponseDTO> addCourse(@Valid @RequestBody CourseRequestDTO requestDTO) {
        log.info("Admin request: create course");

        Course course = modelMapper.map(requestDTO, Course.class);
        Course saved = adminService.addCourse(course);

        log.info("Course created successfully with id {}", saved.getId());
        return ResponseEntity.ok(modelMapper.map(saved, CourseResponseDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDTO requestDTO) {

        log.info("Admin request: update course {}", id);

        Course course = modelMapper.map(requestDTO, Course.class);
        Course updated = adminService.updateCourse(id, course);

        if (updated == null) {
            log.warn("Course not found for update, id={}", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Course updated successfully, id={}", id);
        return ResponseEntity.ok(modelMapper.map(updated, CourseResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        log.warn("Admin request: delete course {}", id);
        return ResponseEntity.ok(adminService.deleteCourse(id));
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> getPagedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Admin request: get paged courses");
        log.debug("page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

        Page<Course> coursePage =
                adminService.getAllCoursesPaged(page, size, sortBy, sortDir);

        return ResponseEntity.ok(coursePage.map(c ->
                modelMapper.map(c, CourseResponseDTO.class)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        log.info("Admin request: get course {}", id);

        Course course = adminService.getCourseById(id);
        if (course == null) {
            log.warn("Course not found, id={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(modelMapper.map(course, CourseResponseDTO.class));
    }
}
