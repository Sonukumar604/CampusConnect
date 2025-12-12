package com.example.CampusConnect.model;

import com.example.CampusConnect.model.HackathonStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @NotBlank private String title;

    @NotBlank private String technology;

    @NotBlank private String organization;

    private String location;

    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private HackathonStatus status;

    @Min(0)
    private Integer participantLimit; // null or 0 = unlimited

    private boolean registrationOpen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    public void assignCreator(User user) {
        this.createdBy = user;
        user.getHackathonsCreated().add(this);
    }

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HackathonRegistration> registrations = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "hackathon_judges",
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> judges = new HashSet<>();


    private boolean active = true;

    private boolean registrationsOpen = true;

    private int registeredParticipantsCount = 0;
}
