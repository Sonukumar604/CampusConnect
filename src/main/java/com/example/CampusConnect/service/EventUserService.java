package com.example.CampusConnect.service;


import com.example.CampusConnect.dto.EventDTO;
import com.example.CampusConnect.util.PagedResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface EventUserService {

    PagedResponse<EventDTO> listPublishedEvents(int page, int size, String sortBy, String sortDir);

    List<EventDTO> upcomingEvents(int limit);

    PagedResponse<EventDTO> filterEvents(int page, int size, String sortBy, String sortDir,
                                         String type, String mode, String location,
                                         LocalDateTime from, LocalDateTime to);
    EventDTO getEventDetails(Long eventId);
}
