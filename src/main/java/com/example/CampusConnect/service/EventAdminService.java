package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.CreateEventDTO;
import com.example.CampusConnect.dto.EventDTO;
import com.example.CampusConnect.dto.UpdateEventDTO;
import com.example.CampusConnect.util.PagedResponse;
import org.springframework.data.domain.Page;

public interface EventAdminService {

    EventDTO createEvent(Long adminId, CreateEventDTO dto);

    EventDTO updateEvent(Long adminId, UpdateEventDTO dto);

    void deleteEvent(Long adminId, Long eventId);

    EventDTO getEventById(Long eventId);

    PagedResponse<EventDTO> getEventsPaged(int page, int size, String sortBy, String sortDir, String filterType);
}
