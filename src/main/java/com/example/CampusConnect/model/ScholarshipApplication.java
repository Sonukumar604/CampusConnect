package com.example.CampusConnect.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scholarship_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScholarshipApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scholarship_id")
    private Scholarship scholarship;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private com.example.CampusConnect.model.User user;

    private LocalDateTime appliedAt;
    private String status; // OR enum if you have RegistrationStatus
}
