package com.example.CampusConnect.service;


import com.example.CampusConnect.model.User;

public interface EmailVerificationService {

    void createVerificationToken(User user);

    void verifyEmail(String token);

}