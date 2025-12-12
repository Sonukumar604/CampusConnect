package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.Course;
import com.example.CampusConnect.model.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByDomainIgnoreCase(String domain);

    List<Course> findByTechnologyIgnoreCase(String technology);

    List<Course> findByInstructorIgnoreCase(String instructor);

    List<Course> findByFreeTrue();

    List<Course> findByCourseType(CourseType courseType);
}
