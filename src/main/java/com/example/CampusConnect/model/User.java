package com.example.CampusConnect.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseAuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= BASIC FIELDS ================= */

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Version
    private Long version;

    /* ================= RELATIONSHIPS ================= */

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Hackathon> hackathonsCreated = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<HackathonRegistration> hackathonRegistrations = new ArrayList<>();

    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL)
    private List<Internship> internshipsPosted = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<InternshipApplication> internshipApplications = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Course> coursesCreated = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CourseEnrollment> courseEnrollments = new ArrayList<>();

    // ✅ FIX: mappedBy now matches Hackathon.judges
    @ManyToMany(mappedBy = "judges")
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
