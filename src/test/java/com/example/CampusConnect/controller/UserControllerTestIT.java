package com.example.CampusConnect.controller;

import com.example.CampusConnect.config.ContainersTestConfiguration;
import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.security.CustomUserDetails;
import com.example.CampusConnect.security.jwt.JwtService;
import com.example.CampusConnect.security.oauth.model.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(ContainersTestConfiguration.class)
class UserControllerTestIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .name("Hero")
                .email("hero-" + UUID.randomUUID() + "@gmail.com")
                .password("password123")
                .role(Role.ADMIN)
                .status(User.Status.ACTIVE)
                .provider(AuthProvider.GOOGLE)
                .enabled(true)
                .emailVerified(true)
                .createdBy("test-user")
                .build();
    }

    @Test
    void tesGetUserById_success(){
        User savedUser = userRepository.save(testUser);
        String accessToken = jwtService.generateAccessToken(new CustomUserDetails(savedUser));

        webTestClient.get()
                .uri("/api/users/{id}", savedUser.getId())
                .cookie("CC_ACCESS_TOKEN", accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.data.id").isEqualTo(savedUser.getId().intValue())
                .jsonPath("$.data.name").isEqualTo(savedUser.getName())
                .jsonPath("$.data.email").isEqualTo(savedUser.getEmail())
                .jsonPath("$.data.role").isEqualTo(savedUser.getRole().name());
    }
    @Test
    void testGetUserById_failure(){
        User savedUser = userRepository.save(testUser);
        String accessToken = jwtService.generateAccessToken(new CustomUserDetails(savedUser));

        webTestClient.get()
                .uri("/api/users/{id}", 9999)
                .cookie("CC_ACCESS_TOKEN", accessToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found with ID: 9999");
    }

    @Test
    void testRegisterUser_success() {
        User savedAdmin = userRepository.save(testUser);
        String accessToken = jwtService.generateAccessToken(new CustomUserDetails(savedAdmin));

        String newEmail = "new-user-" + UUID.randomUUID() + "@gmail.com";

        webTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("CC_ACCESS_TOKEN", accessToken)
                .bodyValue(Map.of(
                        "name", "New User",
                        "email", newEmail,
                        "password", "password123",
                        "role", "student"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("User registered successfully")
                .jsonPath("$.data.name").isEqualTo("New User")
                .jsonPath("$.data.email").isEqualTo(newEmail)
                .jsonPath("$.data.role").isEqualTo("STUDENT");
    }

    @Test
    void testRegisterUser_failure_duplicateEmail() {
        User savedAdmin = userRepository.save(testUser);
        String accessToken = jwtService.generateAccessToken(new CustomUserDetails(savedAdmin));

        webTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("CC_ACCESS_TOKEN", accessToken)
                .bodyValue(Map.of(
                        "name", "Duplicate User",
                        "email", savedAdmin.getEmail(),
                        "password", "password123",
                        "role", "student"
                ))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Email already exists");
    }
    

}
