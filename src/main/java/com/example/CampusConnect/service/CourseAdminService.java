package com.example.CampusConnect.service;

import com.example.CampusConnect.model.Course;
import org.springframework.data.domain.Page;

public interface CourseAdminService {

    Course addCourse(Course course);

    Course updateCourse(Long id, Course updatedCourse);

    String deleteCourse(Long id);

    Page<Course> getAllCoursesPaged(int page, int size, String sortBy, String sortDir);

    Course getCourseById(Long id);
}
