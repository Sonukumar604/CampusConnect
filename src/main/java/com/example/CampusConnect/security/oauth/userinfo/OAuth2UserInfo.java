package com.example.CampusConnect.security.oauth.userinfo;

public interface OAuth2UserInfo {
    String getId();

    String getEmail();

    String getName();

    String getImageUrl();
}
