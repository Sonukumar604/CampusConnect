package com.example.CampusConnect.security;

import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    private Authentication getAuthentication() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated user");
        }
        return authentication;
    }

    public String getCurrentUserEmail() {
        return getAuthentication().getName();
    }

    public User getCurrentUser() {

        String email = getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found in database"));
    }
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}