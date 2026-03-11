package com.example.CampusConnect.security.permission;

import com.example.CampusConnect.model.Role;

import java.util.*;

public class RolePermissionUtil {

    private static final Map<Role, Set<Permission>> BASE_ROLE_PERMISSIONS = Map.of(

            Role.ADMIN, Set.of(
                    Permission.USER_VIEW,
                    Permission.USER_MANAGE,
                    Permission.PLATFORM_ANALYTICS
            ),

            Role.ORGANIZER, Set.of(
                    Permission.HACKATHON_CREATE,
                    Permission.HACKATHON_UPDATE,
                    Permission.INTERNSHIP_CREATE,
                    Permission.INTERNSHIP_UPDATE,
                    Permission.COURSE_CREATE,
                    Permission.COURSE_UPDATE,
                    Permission.EVENT_CREATE,
                    Permission.EVENT_UPDATE
            ),

            Role.STUDENT, Set.of(
                    Permission.HACKATHON_REGISTER,
                    Permission.INTERNSHIP_APPLY,
                    Permission.COURSE_ENROLL,
                    Permission.EVENT_REGISTER
            )
    );

    public static Set<Permission> getPermissions(Role role) {

        Set<Permission> permissions = new HashSet<>();

        permissions.addAll(BASE_ROLE_PERMISSIONS.getOrDefault(role, Set.of()));

        Set<Role> inheritedRoles = RoleHierarchyUtil.ROLE_HIERARCHY.getOrDefault(role, Set.of());

        for (Role inherited : inheritedRoles) {
            permissions.addAll(BASE_ROLE_PERMISSIONS.getOrDefault(inherited, Set.of()));
        }

        return permissions;
    }
}