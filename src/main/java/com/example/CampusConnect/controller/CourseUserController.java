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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/courses")
public class CourseUserController {

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
        Page<Course> courses = userService.getAllCoursesPaged(page, size, sortBy, sortDir);
        Page<CourseResponseDTO> dtoPage = courses.map(c -> modelMapper.map(c, CourseResponseDTO.class));
        return ResponseEntity.ok(dtoPage);
    }

    // Free courses
    @GetMapping("/free")
    public ResponseEntity<List<CourseResponseDTO>> getFreeCourses() {
        List<Course> list = userService.getFreeCourses();
        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Filter by domain
    @GetMapping("/filter/domain/{domain}")
    public ResponseEntity<List<CourseResponseDTO>> filterByDomain(@PathVariable String domain) {
        List<Course> list = userService.filterByDomain(domain);
        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Filter by technology
    @GetMapping("/filter/technology/{tech}")
    public ResponseEntity<List<CourseResponseDTO>> filterByTechnology(@PathVariable("tech") String tech) {
        List<Course> list = userService.filterByTechnology(tech);
        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Filter by instructor
    @GetMapping("/filter/instructor/{instructor}")
    public ResponseEntity<List<CourseResponseDTO>> filterByInstructor(@PathVariable String instructor) {
        List<Course> list = userService.filterByInstructor(instructor);
        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Filter by course type (enum)
    @GetMapping("/filter/type/{type}")
    public ResponseEntity<List<CourseResponseDTO>> filterByType(@PathVariable CourseType type) {
        List<Course> list = userService.filterByCourseType(type);
        List<CourseResponseDTO> dtos = list.stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Get single course by id
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        Course course = userService.getCourseById(id);
        if (course == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(modelMapper.map(course, CourseResponseDTO.class));
    }
    @PostMapping("/{courseId}/enroll/{userId}")
    public ResponseEntity<CourseEnrollmentDTO> enroll(
            @PathVariable Long courseId,
            @PathVariable Long userId,
            @Valid @RequestBody CourseEnrollmentRequest request) {

        return ResponseEntity.ok(userService.enroll(userId, courseId, request));
    }

    // Fetch user's enrollments
    @GetMapping("/enrollments/{userId}")
    public ResponseEntity<List<CourseEnrollmentDTO>> myEnrollments(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.myEnrollments(userId));
    }
}
