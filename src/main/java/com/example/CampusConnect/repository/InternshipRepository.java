package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.Internship;
import com.example.CampusConnect.model.InternshipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {

    // 🔍 Filter by location
    Page<Internship> findByLocationIgnoreCase(String location, Pageable pageable);

    // 🔍 Filter by internship type
    Page<Internship> findByType(InternshipType type, Pageable pageable);

    // 🔍 Search by company name or role
    @Query("SELECT i FROM Internship i WHERE " +
            "LOWER(i.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.role) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Internship> searchInternships(@Param("keyword") String keyword, Pageable pageable);
}
