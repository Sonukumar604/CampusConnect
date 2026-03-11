package com.example.CampusConnect.security.permission;

public enum Permission {

    // ===== USER MANAGEMENT =====
    USER_VIEW,
    USER_MANAGE,

    // ===== HACKATHON =====
    HACKATHON_CREATE,
    HACKATHON_UPDATE,
    HACKATHON_DELETE,
    HACKATHON_REGISTER,

    // ===== INTERNSHIP =====
    INTERNSHIP_CREATE,
    INTERNSHIP_UPDATE,
    INTERNSHIP_DELETE,
    INTERNSHIP_APPLY,

    // ===== COURSE =====
    COURSE_CREATE,
    COURSE_UPDATE,
    COURSE_DELETE,
    COURSE_ENROLL,

    // ===== EVENT =====
    EVENT_CREATE,
    EVENT_UPDATE,
    EVENT_DELETE,
    EVENT_REGISTER,

    // ===== PLATFORM ADMIN =====
    PLATFORM_ANALYTICS
}