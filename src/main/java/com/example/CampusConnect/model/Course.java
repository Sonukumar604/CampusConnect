package com.example.CampusConnect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Audited // 🔥 Enable Hibernate Envers for this entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @NotBlank(message = "Course title is required")
    private String title;

    @NotBlank(message = "Instructor name is required")
    private String instructor;

    @NotBlank(message = "Platform is required (YouTube, Coursera, etc.)")
    private String platform;

    @NotBlank(message = "Domain is required (DSA, Web-Dev, AI-ML, etc.)")
    private String domain;

    @NotBlank(message = "Technology is required")
    private String technology;

    @NotBlank(message = "Level is required (Beginner, Intermediate, Advanced)")
    private String level;

    @NotNull(message = "Duration (weeks) is required")
    @Min(value = 1, message = "Duration must be at least 1 week")
    private Integer durationWeeks;

    private boolean free;

    private Double price;

    private String url;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseType courseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = false)
    @NotAudited // ❌ Do NOT audit collections
    private List<CourseEnrollment> enrollments = new ArrayList<>();
}
