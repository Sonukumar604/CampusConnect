package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.Hackathon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;

public interface HackathonRepository extends JpaRepository<Hackathon, Long> {

    // 🔒 PESSIMISTIC LOCK (registration safety)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from Hackathon h where h.id = :id")
    Optional<Hackathon> findByIdForUpdate(@Param("id") Long id);

    // 🔍 FILTER SUPPORT (used by HackathonUserServiceImpl)
    @Query("""
        select h from Hackathon h
        where (:technology is null or h.technology = :technology)
          and (:organization is null or h.organization = :organization)
          and (:startDate is null or h.startDate >= :startDate)
    """)
    Page<Hackathon> filterHackathons(
            @Param("technology") String technology,
            @Param("organization") String organization,
            @Param("startDate") LocalDate startDate,
            Pageable pageable
    );
}
