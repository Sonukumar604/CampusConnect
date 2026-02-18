package com.example.CampusConnect.service;

import com.example.CampusConnect.dto.LoginRequestDTO;
import com.example.CampusConnect.dto.LoginResponseDTO;
import com.example.CampusConnect.dto.SignupRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    void signup(SignupRequestDTO signupRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO,
                           HttpServletResponse response);

    LoginResponseDTO refreshToken(HttpServletRequest request,
                                  HttpServletResponse response);

    void logout(HttpServletRequest request,
                HttpServletResponse response);
}
