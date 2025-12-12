package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CourseEnrollmentDTO;
import com.example.CampusConnect.dto.CourseEnrollmentRequest;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.*;
import com.example.CampusConnect.repository.CourseEnrollmentRepository;
import com.example.CampusConnect.repository.CourseRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.CourseUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseUserServiceImpl implements CourseUserService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private final UserRepository userRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final ModelMapper mapper;

    // ✅ Get all courses (paginated + sorted)
    @Override
    public Page<Course> getAllCoursesPaged(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return courseRepository.findAll(pageable);
    }

    // ✅ Filter by Domain
    @Override
    public List<Course> filterByDomain(String domain) {
        return courseRepository.findByDomainIgnoreCase(domain);
    }

    // ✅ Filter by Technology
    @Override
    public List<Course> filterByTechnology(String technology) {
        return courseRepository.findByTechnologyIgnoreCase(technology);
    }

    // ✅ Filter by Instructor
    @Override
    public List<Course> filterByInstructor(String instructor) {
        return courseRepository.findByInstructorIgnoreCase(instructor);
    }

    // ✅ Filter by Enum CourseType
    @Override
    public List<Course> filterByCourseType(CourseType courseType) {
        return courseRepository.findByCourseType(courseType);
    }

    // ✅ Get only Free courses
    @Override
    public List<Course> getFreeCourses() {
        return courseRepository.findByFreeTrue();
    }

    // ✅ Get course details by ID
    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    @Override
    public CourseEnrollmentDTO enroll(Long userId, Long courseId, CourseEnrollmentRequest request) {

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new IllegalArgumentException("Already enrolled");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        CourseEnrollment enrollment = CourseEnrollment.builder()
                .user(user)
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .build();

        CourseEnrollment saved = enrollmentRepository.save(enrollment);

        return mapper.map(saved, CourseEnrollmentDTO.class);
    }

    @Override
    public List<CourseEnrollmentDTO> myEnrollments(Long userId) {
        return enrollmentRepository.findByUserId(userId)
                .stream()
                .map(e -> mapper.map(e, CourseEnrollmentDTO.class))
                .collect(Collectors.toList());
    }
}
