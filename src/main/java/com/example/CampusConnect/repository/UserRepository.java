package com.example.CampusConnect.repository;


import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.hackathonsCreated WHERE u.id = :id")
    Optional<User> findUserWithHackathons(Long id);

    List<User> findByRole(Role role);

}
