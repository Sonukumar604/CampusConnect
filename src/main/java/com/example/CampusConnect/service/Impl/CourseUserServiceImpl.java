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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")              // Only USER can access
@Transactional(readOnly = true)              // Default: read-only optimization
public class CourseUserServiceImpl implements CourseUserService {

    private static final Logger log =
            LoggerFactory.getLogger(CourseUserServiceImpl.class);

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final ModelMapper mapper;

    /* ============================================================
       READ OPERATIONS (optimized via readOnly=true)
       ============================================================ */

    @Override
    public Page<Course> getAllCoursesPaged(int page, int size, String sortBy, String sortDir) {

        log.info("Fetching courses | page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return courseRepository.findAll(pageable);
    }

    @Override
    public List<Course> filterByDomain(String domain) {
        log.info("Filtering courses by domain='{}'", domain);
        return courseRepository.findByDomainIgnoreCase(domain);
    }

    @Override
    public List<Course> filterByTechnology(String technology) {
        log.info("Filtering courses by technology='{}'", technology);
        return courseRepository.findByTechnologyIgnoreCase(technology);
    }

    @Override
    public List<Course> filterByInstructor(String instructor) {
        log.info("Filtering courses by instructor='{}'", instructor);
        return courseRepository.findByInstructorIgnoreCase(instructor);
    }

    @Override
    public List<Course> filterByCourseType(CourseType courseType) {
        log.info("Filtering courses by courseType={}", courseType);
        return courseRepository.findByCourseType(courseType);
    }

    @Override
    public List<Course> getFreeCourses() {
        log.info("Fetching all free courses");
        return courseRepository.findByFreeTrue();
    }

    @Override
    public Course getCourseById(Long id) {
        log.info("Fetching course details | courseId={}", id);

        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    @Override
    public List<CourseEnrollmentDTO> myEnrollments(Long userId) {

        log.info("Fetching enrollments for userId={}", userId);

        return enrollmentRepository.findByUserId(userId)
                .stream()
                .map(enrollment -> mapper.map(enrollment, CourseEnrollmentDTO.class))
                .collect(Collectors.toList());
    }

    /* ============================================================
       WRITE OPERATION (requires full transaction)
       ============================================================ */

    @Override
    @Transactional     //  Overrides readOnly=true for write operation
    public CourseEnrollmentDTO enroll(Long userId,
                                      Long courseId,
                                      CourseEnrollmentRequest request) {

        log.info("User {} attempting to enroll in course {}", userId, courseId);

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            log.warn("User {} already enrolled in course {}", userId, courseId);
            throw new IllegalArgumentException("Already enrolled in this course");
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

        log.info("Enrollment successful | userId={}, courseId={}", userId, courseId);

        return mapper.map(saved, CourseEnrollmentDTO.class);
    }
}

