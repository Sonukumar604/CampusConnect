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

@RestController
@RequestMapping("/api/admin/courses")
public class CourseAdminController {

    @Autowired
    private CourseAdminService adminService;

    @Autowired
    private ModelMapper modelMapper;

    // Create course (admin)
    @PostMapping
    public ResponseEntity<CourseResponseDTO> addCourse(@Valid @RequestBody CourseRequestDTO requestDTO) {
        Course course = modelMapper.map(requestDTO, Course.class);
        Course saved = adminService.addCourse(course);
        CourseResponseDTO response = modelMapper.map(saved, CourseResponseDTO.class);
        return ResponseEntity.ok(response);
    }

    // Update course (admin)
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDTO requestDTO) {

        Course course = modelMapper.map(requestDTO, Course.class);
        Course updated = adminService.updateCourse(id, course);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(modelMapper.map(updated, CourseResponseDTO.class));
    }

    // Delete course (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        String msg = adminService.deleteCourse(id);
        return ResponseEntity.ok(msg);
    }

    // Admin paged view (with sorting)
    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> getPagedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<Course> coursePage = adminService.getAllCoursesPaged(page, size, sortBy, sortDir);
        Page<CourseResponseDTO> dtoPage = coursePage.map(c -> modelMapper.map(c, CourseResponseDTO.class));
        return ResponseEntity.ok(dtoPage);
    }

    // Get single course by id (admin)
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        Course course = adminService.getCourseById(id);
        if (course == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(modelMapper.map(course, CourseResponseDTO.class));
    }
}
