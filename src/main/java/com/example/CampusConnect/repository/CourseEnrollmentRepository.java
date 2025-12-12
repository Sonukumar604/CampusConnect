package com.example.CampusConnect.repository;



import com.example.CampusConnect.model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    List<CourseEnrollment> findByUserId(Long userId);

    Optional<CourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}
