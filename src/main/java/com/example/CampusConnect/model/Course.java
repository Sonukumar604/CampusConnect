package com.example.CampusConnect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    // ✅ Enum Field for course type
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseType courseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<CourseEnrollment> enrollments = new ArrayList<>();

}
