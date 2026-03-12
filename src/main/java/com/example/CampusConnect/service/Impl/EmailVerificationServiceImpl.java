package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.model.EmailVerificationToken;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.EmailVerificationTokenRepository;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.service.EmailService;
import com.example.CampusConnect.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public void createVerificationToken(User user) {

        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken =
                EmailVerificationToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(Instant.now().plusSeconds(86400))
                        .used(false)
                        .build();

        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    @Override
    public void verifyEmail(String token) {

        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid verification token"));

        if (verificationToken.isUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = verificationToken.getUser();

        user.setEmailVerified(true);
        user.setEnabled(true);

        userRepository.save(user);

        verificationToken.setUsed(true);

        tokenRepository.save(verificationToken);
    }
}