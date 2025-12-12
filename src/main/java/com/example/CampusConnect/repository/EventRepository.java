package com.example.CampusConnect.repository;


import com.example.CampusConnect.model.Event;
import com.example.CampusConnect.model.EventType;
import com.example.CampusConnect.model.EventMode;
import com.example.CampusConnect.model.PublishStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByPublishStatus(PublishStatus status, Pageable pageable);

    Page<Event> findByEventTypeAndPublishStatus(EventType type, PublishStatus status, Pageable pageable);

    Page<Event> findByModeAndPublishStatus(EventMode mode, PublishStatus status, Pageable pageable);

    Page<Event> findByStartDateTimeBetweenAndPublishStatus(LocalDateTime from, LocalDateTime to, PublishStatus status, Pageable pageable);

    // combine searches as needed in service layer using criteria or manual filters
}

