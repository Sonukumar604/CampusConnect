package com.example.CampusConnect.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scholarships")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scholarship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ScholarshipCategory category;

    @Column(length = 1000)
    private String eligibilityCriteria;

    private Double amount;

    private LocalDate deadline;

    private String provider;

    @Enumerated(EnumType.STRING)
    private PublishStatus publishStatus = PublishStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private com.example.CampusConnect.model.User createdBy;

    @OneToMany(mappedBy = "scholarship", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScholarshipApplication> applications = new ArrayList<>();
}
