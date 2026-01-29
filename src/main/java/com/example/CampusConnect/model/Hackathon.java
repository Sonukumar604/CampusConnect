package com.example.CampusConnect.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hackathons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hackathon extends BaseAuditableEntity {

    /* ================= PRIMARY KEY ================= */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= LOCKING ================= */

    @Version // 🔒 Optimistic Locking
    private Long version;

    /* ================= BASIC FIELDS ================= */

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

    private boolean registrationsOpen = true;

    private boolean active = true;

    private int registeredParticipantsCount = 0;

    /* ================= RELATIONSHIPS ================= */

    /**
     * Business ownership (NOT auditing).
     * Auditing uses BaseAuditableEntity.createdBy (String).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @OneToMany(
            mappedBy = "hackathon",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
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
        this.createdByUser = user;
        user.getHackathonsCreated().add(this);
    }
}
