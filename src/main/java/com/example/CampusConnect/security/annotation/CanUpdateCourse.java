package com.example.CampusConnect.security.annotation;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("hasAuthority('COURSE_UPDATE')")
public @interface CanUpdateCourse {
}