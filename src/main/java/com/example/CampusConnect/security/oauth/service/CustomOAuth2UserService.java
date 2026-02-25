package com.example.CampusConnect.security.oauth.service;

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

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User oauth2User = super.loadUser(request);

        String registrationId =
                request.getClientRegistration().getRegistrationId();

        AuthProvider provider =
                AuthProvider.valueOf(registrationId.toUpperCase());

        OAuth2UserInfo userInfo =
                OAuth2UserInfoFactory.getOAuth2UserInfo(
                        provider,
                        oauth2User.getAttributes()
                );

        String email = userInfo.getEmail();

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("Email not found from provider");
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(userInfo, provider));

        return new DefaultOAuth2User(
                oauth2User.getAuthorities(),
                oauth2User.getAttributes(),
                "email"
        );
    }

    private User registerNewUser(OAuth2UserInfo userInfo,
                                 AuthProvider provider) {

        User user = new User();
        user.setEmail(userInfo.getEmail());
        user.setName(userInfo.getName());
        user.setProvider(provider);
        user.setImageUrl(userInfo.getImageUrl());

        return userRepository.save(user);
    }
}