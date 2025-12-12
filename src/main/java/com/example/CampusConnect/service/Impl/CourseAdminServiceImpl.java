package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.model.Course;
import com.example.CampusConnect.repository.CourseRepository;
import com.example.CampusConnect.service.CourseAdminService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CourseAdminServiceImpl implements CourseAdminService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Course addCourse(Course course) {
        // If marked as free, ensure price = 0
        if (course.isFree()) {
            course.setPrice(0.0);
        }
        // Save course
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Long id, Course updatedCourse) {
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
            return courseRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public String deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return "Course deleted successfully.";
        }
        return "Course not found.";
    }

    @Override
    public Page<Course> getAllCoursesPaged(int page, int size, String sortBy, @NotNull String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return courseRepository.findAll(pageable);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }
}
