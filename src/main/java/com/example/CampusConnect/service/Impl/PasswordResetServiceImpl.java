package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.exceptions.ResourceNotFoundException;
import com.example.CampusConnect.model.PasswordResetToken;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.PasswordResetTokenRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final long RESET_TOKEN_EXPIRY_SECONDS = 3600; // 1 hour

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createResetToken(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));

        // invalidate old tokens (security best practice)
        tokenRepository.invalidateUserTokens(user.getId());

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(Instant.now().plusSeconds(RESET_TOKEN_EXPIRY_SECONDS))
                        .used(false)
                        .build();

        tokenRepository.save(resetToken);

        // Later replace with real email service
        System.out.println("Password reset link: http://localhost:8080/api/auth/reset-password?token=" + token);
    }

    @Override
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Invalid password reset token"));

        if (resetToken.isUsed()) {
            throw new IllegalStateException("Password reset token already used");
        }

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new IllegalStateException("Password reset token expired");
        }

        User user = resetToken.getUser();

        // update password
        user.setPassword(passwordEncoder.encode(newPassword));

        // invalidate all existing JWT tokens
        user.setTokenVersion(user.getTokenVersion() + 1);

        userRepository.save(user);

        // mark token used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}