package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.CreateInternshipDTO;
import com.example.CampusConnect.dto.InternshipDTO;
import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.Internship;
import com.example.CampusConnect.model.NotificationType;
import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.InternshipRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.InternshipAdminService;
import com.example.CampusConnect.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class InternshipAdminServiceImpl implements InternshipAdminService {

    private static final Logger log =
            LoggerFactory.getLogger(InternshipAdminServiceImpl.class);

    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // =========================
    // CREATE
    // =========================
    @Override
    @Transactional
    public InternshipDTO createInternship(CreateInternshipDTO dto) {

        log.info("Admin creating new internship");

        Internship internship = new Internship();
        BeanUtils.copyProperties(dto, internship);

        internship.setPostedOn(LocalDate.now());

        Internship saved = internshipRepository.save(internship);

        log.info("Internship created successfully with id={}", saved.getId());

        // Notify all students
        List<User> students = userRepository.findByRole(Role.STUDENT);

        for (User student : students) {

            notificationService.createNotification(
                    student,
                    "New Internship Posted",
                    saved.getRole() + " internship at " + saved.getCompanyName(),
                    NotificationType.INTERNSHIP
            );

        }

        return mapToDto(saved);
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    @Transactional
    public InternshipDTO updateInternship(Long id, CreateInternshipDTO dto) {

        log.info("Admin updating internship with id={}", id);

        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Internship update failed: id={} not found", id);
                    return new ResourceNotFoundException("Internship not found");
                });

        BeanUtils.copyProperties(dto, internship, "id", "postedOn");

        Internship updated = internshipRepository.save(internship);

        log.info("Internship updated successfully with id={}", updated.getId());

        return mapToDto(updated);
    }

    // =========================
    // DELETE
    // =========================
    @Override
    @Transactional
    public void deleteInternship(Long id) {

        log.info("Admin deleting internship with id={}", id);

        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Internship delete failed: id={} not found", id);
                    return new ResourceNotFoundException("Internship not found");
                });

        internshipRepository.delete(internship);

        log.info("Internship deleted successfully with id={}", id);
    }

    // =========================
    // MAPPER
    // =========================
    private InternshipDTO mapToDto(Internship internship) {

        InternshipDTO dto = new InternshipDTO();
        BeanUtils.copyProperties(internship, dto);

        return dto;
    }
}
