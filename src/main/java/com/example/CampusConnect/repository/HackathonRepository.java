package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.Hackathon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {

    // For filtering hackathons dynamically
    @Query("""
       SELECT h FROM Hackathon h
       WHERE (:technology IS NULL OR h.technology LIKE %:technology%)
         AND (:organization IS NULL OR h.organization LIKE %:organization%)
         AND (:startDate IS NULL OR h.startDate = :startDate)
       """)
    Page<Hackathon> filterHackathons(
            @Param("technology") String technology,
            @Param("organization") String organization,
            @Param("startDate") LocalDate startDate,
            Pageable pageable
    );


    // Check if hackathon is active
    List<Hackathon> findByActiveTrue();

    // For user side sorted results
    Page<Hackathon> findByActiveTrue(Pageable pageable);
}

