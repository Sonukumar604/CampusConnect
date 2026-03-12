package com.example.CampusConnect.service;


public interface EmailService {

    void sendVerificationEmail(String email, String token);

}