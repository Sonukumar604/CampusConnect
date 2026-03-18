package com.example.CampusConnect.model;

import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scholarships")
@Audited // ✅ Enable Hibernate Envers
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor

public class Scholarship extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version // 🔒 Optimistic Locking
    private Long version;

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
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @OneToMany(mappedBy = "scholarship", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited // ❌ Avoid auditing transactional data
    private List<ScholarshipApplication> applications = new ArrayList<>();
}
