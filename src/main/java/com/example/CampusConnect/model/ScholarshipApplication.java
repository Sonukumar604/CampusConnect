package com.example.CampusConnect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @JoinColumn(name = "scholarship_id", nullable = false)
    @NotNull(message = "Scholarship is required")
    private Scholarship scholarship;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    @Column(length = 500, nullable = false)
    @NotBlank(message = "Statement of purpose is required")
    @Size(min = 20, max = 500)
    private String sop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;
}
