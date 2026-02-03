package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.model.Course;
import com.example.CampusConnect.repository.CourseRepository;
import com.example.CampusConnect.service.CourseAdminService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class CourseAdminServiceImpl implements CourseAdminService {

    private static final Logger log =
            LoggerFactory.getLogger(CourseAdminServiceImpl.class);

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Course addCourse(Course course) {
        log.info("Creating new course with title='{}'", course.getTitle());

        // If marked as free, ensure price = 0
        if (course.isFree()) {
            log.debug("Course marked as free. Setting price to 0");
            course.setPrice(0.0);
        }

        Course saved = courseRepository.save(course);
        log.info("Course created successfully with ID {}", saved.getId());
        return saved;
    }

    @Override
    public Course updateCourse(Long id, Course updatedCourse) {
        log.info("Updating course with ID {}", id);

        return courseRepository.findById(id).map(existing -> {
            existing.setTitle(updatedCourse.getTitle());
            existing.setInstructor(updatedCourse.getInstructor());
            existing.setPlatform(updatedCourse.getPlatform());
            existing.setDomain(updatedCourse.getDomain());
            existing.setTechnology(updatedCourse.getTechnology());
            existing.setLevel(updatedCourse.getLevel());
            existing.setDurationWeeks(updatedCourse.getDurationWeeks());
            existing.setFree(updatedCourse.isFree());
            existing.setPrice(updatedCourse.getPrice());
            existing.setUrl(updatedCourse.getUrl());
            existing.setDescription(updatedCourse.getDescription());
            existing.setCourseType(updatedCourse.getCourseType());

            Course saved = courseRepository.save(existing);
            log.info("Course updated successfully with ID {}", id);
            return saved;
        }).orElseGet(() -> {
            log.warn("Course not found while updating. ID {}", id);
            return null;
        });
    }

    @Override
    public String deleteCourse(Long id) {
        log.info("Deleting course with ID {}", id);

        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            log.info("Course deleted successfully with ID {}", id);
            return "Course deleted successfully.";
        }

        log.warn("Course not found while deleting. ID {}", id);
        return "Course not found.";
    }

    @Override
    public Page<Course> getAllCoursesPaged(
            int page,
            int size,
            String sortBy,
            @NotNull String sortDir
    ) {
        log.info("Fetching paged courses: page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Course> result = courseRepository.findAll(pageable);

        log.info("Fetched {} courses in current page", result.getNumberOfElements());
        return result;
    }

    @Override
    public Course getCourseById(Long id) {
        log.info("Fetching course by ID {}", id);

        Course course = courseRepository.findById(id).orElse(null);

        if (course == null) {
            log.warn("Course not found with ID {}", id);
        } else {
            log.info("Course found with ID {}", id);
        }

        return course;
    }
}
