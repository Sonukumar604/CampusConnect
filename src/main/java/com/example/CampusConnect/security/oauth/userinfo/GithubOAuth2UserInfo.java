package com.example.CampusConnect.security.oauth.userinfo;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getName() {
        Object name = attributes.get("name");
        return name != null ? name.toString()
                : (String) attributes.get("login");
    }

    @Override
    public String getEmail() {
        Object email = attributes.get("email");

        // GitHub often returns null email
        if (email == null) {
            String login = (String) attributes.get("login");
            return login + "@github.local";
        }

        return email.toString();
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}