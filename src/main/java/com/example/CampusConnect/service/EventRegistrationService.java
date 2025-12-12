package com.example.CampusConnect.service;



import com.example.CampusConnect.dto.EventRegistrationDTO;
import com.example.CampusConnect.dto.EventRegistrationRequest;

import java.util.List;

public interface EventRegistrationService {

    EventRegistrationDTO register(Long userId, Long eventId, EventRegistrationRequest request);

    List<EventRegistrationDTO> myRegistrations(Long userId);

    List<EventRegistrationDTO> listRegistrationsForEvent(Long eventId);

    void cancelRegistration(Long userId, Long eventId);
}
