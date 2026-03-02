
package com.example.CampusConnect.security.oauth.userinfo;

import com.example.CampusConnect.security.oauth.model.AuthProvider;

import java.util.Map;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(
            String registrationId,
            Map<String, Object> attributes) {

        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("github")) {
            return new GithubOAuth2UserInfo(attributes);
        } else {
            throw new IllegalArgumentException(
                    "Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }

}