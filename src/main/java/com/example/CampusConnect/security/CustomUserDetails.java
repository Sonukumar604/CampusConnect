package com.example.CampusConnect.security;

import com.example.CampusConnect.model.Role;
import com.example.CampusConnect.model.User;
import com.example.CampusConnect.security.permission.Permission;
import com.example.CampusConnect.security.permission.RolePermissionUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // ROLE + PERMISSIONS → GrantedAuthority
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();

        // ROLE authority (required for hasRole())
        authorities.add(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        // Permission authorities (for granular access)
        Set<Permission> permissions =
                RolePermissionUtil.getPermissions(user.getRole());

        if (permissions != null) {
            permissions.forEach(permission ->
                    authorities.add(
                            new SimpleGrantedAuthority(permission.name())
                    )
            );
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // EMAIL used as username
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // ===== Account lifecycle =====

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != User.Status.BLOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == User.Status.ACTIVE;
    }

    // ===== Custom getters =====

    public Long getId() {
        return user.getId();
    }

    public String getName() {
        return user.getName();
    }

    public Role getRole() {
        return user.getRole();
    }

    public User getUser() {
        return user;
    }

    public Integer getTokenVersion() {
        return user.getTokenVersion();
    }
}