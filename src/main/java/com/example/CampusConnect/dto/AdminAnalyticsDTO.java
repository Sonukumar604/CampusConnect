
package com.example.CampusConnect.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminAnalyticsDTO {

    private long totalUsers;
    private long totalHackathons;
    private long totalInternships;
    private long totalCourses;

}