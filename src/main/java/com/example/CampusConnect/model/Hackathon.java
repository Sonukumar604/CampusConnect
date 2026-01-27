package com.example.CampusConnect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
@Entity
@Table(name = "hackathons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hackathon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version // 🔒 Optimistic Locking
    private Long version;

    @NotBlank
    private String title;

    @NotBlank
    private String technology;

    @NotBlank
    private String organization;

    private String location;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private HackathonStatus status;

    @Min(0)
    private Integer participantLimit;

    /* ✅ KEEP ONLY THIS FIELD */
    private boolean registrationsOpen = true;

    private boolean active = true;

    private int registeredParticipantsCount = 0;

    /* ================= RELATIONSHIPS ================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HackathonRegistration> registrations = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "hackathon_judges",
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> judges = new HashSet<>();

    /* ================= HELPERS ================= */

    public void assignCreator(User user) {
        this.createdBy = user;
        user.getHackathonsCreated().add(this);
    }
}

