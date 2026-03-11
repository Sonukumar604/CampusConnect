package com.example.CampusConnect.security.permission;


import com.example.CampusConnect.model.Role;

import java.util.Map;
import java.util.Set;

public class RoleHierarchyUtil {

    public static final Map<Role, Set<Role>> ROLE_HIERARCHY = Map.of(

            Role.ADMIN, Set.of(
                    Role.ORGANIZER,
                    Role.STUDENT
            ),

            Role.ORGANIZER, Set.of(
                    Role.STUDENT
            ),

            Role.STUDENT, Set.of()
    );
}