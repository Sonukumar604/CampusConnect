package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Course;
import com.example.CampusConnect.model.NotificationType;
import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.CourseRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.CourseAdminService;
import com.example.CampusConnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CourseAdminServiceImpl implements CourseAdminService {

    private static final Logger log =
            LoggerFactory.getLogger(CourseAdminServiceImpl.class);

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // =========================
    // CREATE
    // =========================
    @Override
    @Transactional
    public Course addCourse(Course course) {

        log.info("Admin creating new course with title='{}'", course.getTitle());

        if (course.isFree()) {
            log.debug("Course marked as free. Setting price to 0");
            course.setPrice(0.0);
        }

        Course saved = courseRepository.save(course);

        log.info("Course created successfully with ID {}", saved.getId());

        // 🔔 Notify all students
        List<User> students = userRepository.findByRole(Role.STUDENT);

        for (User student : students) {
            notificationService.createNotification(
                    student,
                    "New Course Available",
                    saved.getTitle() + " course has been published",
                    NotificationType.COURSE
            );
        }

        return saved;
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    @Transactional
    public Course updateCourse(Long id, Course updatedCourse) {

        log.info("Admin updating course with ID {}", id);

        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course update failed: ID {} not found", id);
                    return new ResourceNotFoundException("Course not found");
                });

        existing.setTitle(updatedCourse.getTitle());
        existing.setInstructor(updatedCourse.getInstructor());
        existing.setPlatform(updatedCourse.getPlatform());
        existing.setDomain(updatedCourse.getDomain());
        existing.setTechnology(updatedCourse.getTechnology());
        existing.setLevel(updatedCourse.getLevel());
        existing.setDurationWeeks(updatedCourse.getDurationWeeks());
        existing.setFree(updatedCourse.isFree());
        existing.setUrl(updatedCourse.getUrl());
        existing.setDescription(updatedCourse.getDescription());
        existing.setCourseType(updatedCourse.getCourseType());

        if (updatedCourse.isFree()) {
            existing.setPrice(0.0);
        } else {
            existing.setPrice(updatedCourse.getPrice());
        }

        Course saved = courseRepository.save(existing);

        log.info("Course updated successfully with ID {}", id);

        return saved;
    }

    // =========================
    // DELETE
    // =========================
    @Override
    @Transactional
    public String deleteCourse(Long id) {

        log.info("Admin deleting course with ID {}", id);

        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course delete failed: ID {} not found", id);
                    return new ResourceNotFoundException("Course not found");
                });

        courseRepository.delete(existing);

        log.info("Course deleted successfully with ID {}", id);

        return "Course deleted successfully.";
    }

    // =========================
    // PAGINATION
    // =========================
    @Override
    public Page<Course> getAllCoursesPaged(
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

        log.info("Fetching paged courses: page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Course> result = courseRepository.findAll(pageable);

        log.info("Fetched {} courses in current page",
                result.getNumberOfElements());

        return result;
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    public Course getCourseById(Long id) {

        log.info("Fetching course by ID {}", id);

        return courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Course not found with ID {}", id);
                    return new ResourceNotFoundException("Course not found");
                });
    }
}