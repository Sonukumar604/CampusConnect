package com.example.CampusConnect.repository;

import com.example.CampusConnect.model.HackathonRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HackathonRegistrationRepository extends JpaRepository<HackathonRegistration, Long> {

    boolean existsByUserId_IdAndHackathonId_Id(Long userId, Long hackathonId);

    List<HackathonRegistration> findByUserId_Id(Long userId);

    List<HackathonRegistration> findByHackathonId_Id(Long hackathonId);
}

