package com.example.CampusConnect.security.oauth.service;

import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.repository.UserRepository;
import com.example.CampusConnect.security.oauth.model.AuthProvider;
import com.example.CampusConnect.security.oauth.userinfo.OAuth2UserInfo;
import com.example.CampusConnect.security.oauth.userinfo.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User oauth2User = super.loadUser(request);

        // 1️⃣ Extract provider (google / github)
        String registrationId =
                request.getClientRegistration().getRegistrationId();

        AuthProvider provider =
                AuthProvider.valueOf(registrationId.toUpperCase(Locale.ROOT));

        // 2️⃣ Extract provider-specific user info
        OAuth2UserInfo userInfo =
                OAuth2UserInfoFactory.getOAuth2UserInfo(
                        registrationId,
                        oauth2User.getAttributes()
                );

        // 3️⃣ Fix email (GitHub may return null)
        String email = userInfo.getEmail();

        if (email == null || email.isBlank()) {
            email = userInfo.getId() + "@github.local";
        }

        String finalEmail = email;

        // 4️⃣ Find or create user
        User user = userRepository.findByEmail(finalEmail)
                .map(existingUser ->
                        updateExistingUser(existingUser, userInfo, provider))
                .orElseGet(() ->
                        registerNewUser(finalEmail, userInfo, provider));

        // 5️⃣ Return Spring Security principal
        Map<String, Object> attributes =
                new HashMap<>(oauth2User.getAttributes());
        attributes.put("email", finalEmail);

        return new DefaultOAuth2User(
                oauth2User.getAuthorities(),
                attributes,
                "email"
        );
    }

    // ===============================
    // REGISTER NEW USER
    // ===============================
    private User registerNewUser(String email,
                                 OAuth2UserInfo userInfo,
                                 AuthProvider provider) {

        User user = new User();

        user.setEmail(email);
        user.setName(userInfo.getName());
        user.setProvider(provider);
        user.setImageUrl(userInfo.getImageUrl());

        // Required NOT NULL fields
        user.setPassword("OAUTH_USER");
        user.setRole(Role.STUDENT);     // default role
        user.setStatus(User.Status.ACTIVE);

        return userRepository.save(user);
    }

    // ===============================
    // UPDATE EXISTING USER
    // ===============================
    private User updateExistingUser(User existingUser,
                                    OAuth2UserInfo userInfo,
                                    AuthProvider provider) {

        // Prevent provider mismatch
        if (existingUser.getProvider() != null &&
                !existingUser.getProvider().equals(provider)) {

            throw new OAuth2AuthenticationException(
                    "You previously signed up with " +
                            existingUser.getProvider() +
                            ". Please use that provider to login."
            );
        }

        existingUser.setName(userInfo.getName());
        existingUser.setImageUrl(userInfo.getImageUrl());

        return userRepository.save(existingUser);
    }
}