package com.example.CampusConnect.security;

import com.example.CampusConnect.handlers.OAuth2SuccessHandler;
import com.example.CampusConnect.security.jwt.JwtAuthenticationFilter;
import com.example.CampusConnect.security.jwt.JwtEntryPoint;
import com.example.CampusConnect.security.jwt.JwtService;
import com.example.CampusConnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtEntryPoint jwtEntryPoint;

    // ==============================
    // OAuth2 Success Handler Bean
    // ==============================
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(
            UserService userService,
            CustomUserDetailsService customUserDetailsService,
            JwtService jwtService
    ) {
        return new OAuth2SuccessHandler(
                userService,
                customUserDetailsService,
                jwtService
        );
    }

    // ==============================
    // Security Filter Chain
    // ==============================
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2SuccessHandler oAuth2SuccessHandler
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtEntryPoint)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/public/**",
                                "/oauth2/**",
                                "/login/**",
                                "/oauth-success.html",
                                "/error"
                        ).permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers("/api/user/**")
                        .hasAnyRole("STUDENT", "ORGANIZER")

                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .oauth2Login(oauth ->
                        oauth.successHandler(oAuth2SuccessHandler)
                );

        return http.build();
    }

    // ==============================
    // Authentication Provider
    // ==============================
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ==============================
    // Password Encoder
    // ==============================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ==============================
    // Authentication Manager
    // ==============================
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}