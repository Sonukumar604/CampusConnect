package com.example.CampusConnect.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity                        //Makes this class a JPA entity (it maps to a database table)
@Table(name = "users")         //Set table name in DB
@Data                          //Lombok annotation for getters, setters etc.
@AllArgsConstructor            //Lombok Constructors
@NoArgsConstructor
@Builder                         //Lombok Constructors
@Getter
@Setter
public class User {
    @Id                                                       // Primary Key (auto-increment)
    @GeneratedValue(strategy = GenerationType.IDENTITY)         //Primary Key.........
    private Long id;

    @Column(nullable = false)
    @JoinColumn(unique = true)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)    // Stores enum as readable string (like "STUDENT")
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;


    public enum Role{
        STUDENT, ORGANIZER, ADMIN
    }

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Hackathon> hackathonsCreated = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<HackathonRegistration> registrations = new ArrayList<>();


    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL)
    private List<Internship> internshipsPosted = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<InternshipApplication> internshipApplications = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Course> coursesCreated = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)//
    private List<CourseEnrollment> courseEnrollments = new ArrayList<>();

    @ManyToMany(mappedBy = "judges")
    private Set<Hackathon> judgingHackathons = new HashSet<>();


    public enum Status {
        ACTIVE,
        BLOCKED,
        DELETED
    }



}
