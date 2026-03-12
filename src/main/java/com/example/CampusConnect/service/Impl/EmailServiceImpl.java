package com.example.CampusConnect.service.Impl;


import com.example.CampusConnect.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendVerificationEmail(String email, String token) {

        String verificationLink =
                "http://localhost:8080/api/auth/verify-email?token=" + token;

        System.out.println("Send email to: " + email);
        System.out.println("Verification link: " + verificationLink);
    }
}