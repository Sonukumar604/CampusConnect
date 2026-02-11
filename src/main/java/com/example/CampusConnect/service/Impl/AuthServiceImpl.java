package com.example.CampusConnect.service.Impl;

import com.example.CampusConnect.dto.LoginRequestDTO;
import com.example.CampusConnect.dto.LoginResponseDTO;
import com.example.CampusConnect.dto.SignupRequestDTO;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.security.CustomUserDetails;
import com.example.CampusConnect.security.jwt.JwtCookieUtil;
import com.example.CampusConnect.security.jwt.JwtService;
import com.example.CampusConnect.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtCookieUtil jwtCookieUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signup(@NotNull SignupRequestDTO dto) {

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(User.Role.STUDENT)
                .status(User.Status.ACTIVE)
                .build();

        userRepository.save(user);
    }

    @Override
    public LoginResponseDTO login(@NotNull LoginRequestDTO dto,
                                  HttpServletResponse response) {

        // 1️⃣ Authenticate user
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getEmail(),
                                dto.getPassword()
                        )
                );

        // 2️⃣ Extract authenticated user
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        // 3️⃣ Generate JWT
        String token = jwtService.generateToken(userDetails);

        // 4️⃣ Store JWT in HttpOnly cookie
        jwtCookieUtil.addJwtCookie(response, token);

        // 5️⃣ Return response (token optional, useful for Postman)
        return new LoginResponseDTO(
                token,
                userDetails.getUser().getId(),
                userDetails.getUser().getName(),
                userDetails.getUsername(),
                userDetails.getUser().getRole()
        );
    }
}
