package com.example.CampusConnect.repository;

import com.example.CampusConnect.config.ContainersTestConfiguration;


import com.example.CampusConnect.config.audit.JpaAuditingConfig;
import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.security.oauth.model.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfig.class, ContainersTestConfiguration.class})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Hero")
                .email("hero@gmail.com")
                .password("password123")
                .role(Role.STUDENT)
                .status(User.Status.ACTIVE)
                .provider(AuthProvider.GOOGLE)
                .enabled(true)
                .emailVerified(true)
                .createdBy("test-user")
                .build();
    }

    @Test
    void findByEmail_whenEmailIsValid_thenReturnUser() {
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("hero@gmail.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void findByEmail_whenEmailIsNotFound_thenReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("notfound@gmail.com");

        assertThat(found).isNotPresent();
    }
}
