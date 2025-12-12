package com.example.CampusConnect.repository;


import com.example.CampusConnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository                                       //Marks this interface as spring-managed component(for database operations)
public interface UserRepository extends JpaRepository<User, Long> { //Inherit  ready-made CRUD methods for your user entity
    Optional<User> findByEmail(String email);//Let your query by email field automatically without sql. Spring will build the sql behind the scene
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.hackathonsCreated WHERE u.id = :id")
    Optional<User> findUserWithHackathons(Long id);

}
