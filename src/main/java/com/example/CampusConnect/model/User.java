package com.example.CampusConnect.model;

import com.example.CampusConnect.security.oauth.model.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.*;

@Entity
@Table(name = "users")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    @NotAudited
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Version
    private Long version;


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

    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.ALL)
    private List<Course> coursesCreated = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @NotAudited
    private List<CourseEnrollment> courseEnrollments = new ArrayList<>();

    @ManyToMany(mappedBy = "judges")
    @NotAudited
    private Set<Hackathon> judgingHackathons = new HashSet<>();

    public enum Status {
        ACTIVE,
        BLOCKED,
        DELETED
    }
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "image_url")
    private String imageUrl;
}
