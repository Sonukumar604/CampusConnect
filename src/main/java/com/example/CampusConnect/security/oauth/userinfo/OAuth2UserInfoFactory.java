
package com.example.CampusConnect.security.oauth.userinfo;

import com.example.CampusConnect.security.oauth.model.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(
            AuthProvider provider,
            Map<String, Object> attributes) {

        return switch (provider) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }
}