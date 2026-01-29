package com.example.CampusConnect.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internships")
@Audited // ✅ Enable Hibernate Envers
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Internship extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version // 🔒 Optimistic Locking
    private Long version;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false, length = 100)
    private String role;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InternshipType type; // REMOTE / HYBRID / ONSITE

    @Column(nullable = false)
    private String stipend;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false)
    private String skillsRequired;

    @Column(nullable = false)
    private String applyLink;

    @Column(nullable = false)
    private LocalDate lastDateToApply;

    @Column(nullable = false)
    private LocalDate postedOn = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @OneToMany(mappedBy = "internship", cascade = CascadeType.ALL, orphanRemoval = false)
    @NotAudited // ❌ Avoid auditing transactional data
    private List<InternshipApplication> applications = new ArrayList<>();
}
