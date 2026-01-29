package com.example.CampusConnect.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.*;

@Entity
@Table(name = "users")
@Audited // ✅ Enable Envers for core User fields
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= BASIC FIELDS ================= */

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @NotAudited // ❌ Never audit passwords
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Version // 🔒 Optimistic Locking
    private Long version;

    /* ================= RELATIONSHIPS ================= */

    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.ALL)
    @NotAudited
    private List<Hackathon> hackathonsCreated = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @NotAudited
    private List<HackathonRegistration> hackathonRegistrations = new ArrayList<>();

    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL)
    @NotAudited
    private List<Internship> internshipsPosted = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @NotAudited
    private List<InternshipApplication> internshipApplications = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    @NotAudited
    private List<Course> coursesCreated = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @NotAudited
    private List<CourseEnrollment> courseEnrollments = new ArrayList<>();

    @ManyToMany(mappedBy = "judges")
    @NotAudited
    private Set<Hackathon> judgingHackathons = new HashSet<>();

    /* ================= ENUMS ================= */

    public enum Role {
        STUDENT,
        ORGANIZER,
        ADMIN
    }

    public enum Status {
        ACTIVE,
        BLOCKED,
        DELETED
    }
}
