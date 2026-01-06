
package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.ScholarshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScholarshipApplicationRepository extends JpaRepository<ScholarshipApplication, Long> {

    List<ScholarshipApplication> findByUserId(Long userId);

    List<ScholarshipApplication> findByScholarshipId(Long scholarshipId);
}
