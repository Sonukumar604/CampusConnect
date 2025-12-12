package com.example.CampusConnect.repository;


import com.example.CampusConnect.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    List<EventRegistration> findByUserId(Long userId);
    List<EventRegistration> findByEventId(Long eventId);
    Optional<EventRegistration> findByUserIdAndEventId(Long userId, Long eventId);
}
