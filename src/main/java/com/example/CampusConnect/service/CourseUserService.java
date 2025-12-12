package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.CourseEnrollmentDTO;
import com.example.CampusConnect.dto.CourseEnrollmentRequest;
import com.example.CampusConnect.model.Course;
import com.example.CampusConnect.model.CourseType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CourseUserService {

    Page<Course> getAllCoursesPaged(int page, int size, String sortBy, String sortDir);

    List<Course> filterByDomain(String domain);

    List<Course> filterByTechnology(String technology);

    List<Course> filterByInstructor(String instructor);

    List<Course> filterByCourseType(CourseType courseType);

    List<Course> getFreeCourses();

    Course getCourseById(Long id);

    CourseEnrollmentDTO enroll(Long userId, Long courseId, CourseEnrollmentRequest request);
    List<CourseEnrollmentDTO> myEnrollments(Long userId);
}
