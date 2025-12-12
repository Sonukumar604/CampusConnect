package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.Scholarship;
import com.example.CampusConnect.model.PublishStatus;
import com.example.CampusConnect.model.ScholarshipCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
    Page<Scholarship> findByPublishStatus(PublishStatus status, Pageable pageable);
    Page<Scholarship> findByCategory(ScholarshipCategory category, Pageable pageable);
    Page<Scholarship> findByProviderContainingIgnoreCase(String provider, Pageable pageable);
    // You can add more custom queries as needed
}
