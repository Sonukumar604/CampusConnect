package com.example.CampusConnect.security;

import com.example.CampusConnect.handlers.OAuth2SuccessHandler;
import com.example.CampusConnect.security.jwt.JwtAuthenticationFilter;
import com.example.CampusConnect.security.jwt.JwtEntryPoint;
import com.example.CampusConnect.security.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
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
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    // ==============================
    // Security Filter Chain
    // ==============================
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtEntryPoint)
                )

                //  VERY IMPORTANT
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/login/**",
                                "/error",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/api/auth/**",
                                "/api/public/**",
                                "/oauth-success.html"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("STUDENT", "ORGANIZER")
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
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