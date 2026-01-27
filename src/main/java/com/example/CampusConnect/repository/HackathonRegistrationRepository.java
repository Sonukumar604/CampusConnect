package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.HackathonRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HackathonRegistrationRepository
        extends JpaRepository<HackathonRegistration, Long> {

    boolean existsByUser_IdAndHackathon_Id(Long userId, Long hackathonId);

    List<HackathonRegistration> findByUser_Id(Long userId);
}
