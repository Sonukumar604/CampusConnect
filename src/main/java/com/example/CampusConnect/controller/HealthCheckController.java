package com.example.CampusConnect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller for AWS Elastic Beanstalk monitoring
 */
@RestController
public class HealthCheckController {

    /**
     * Health check endpoint for load balancer and monitoring
     * Returns 200 OK with "OK" body
     */
    @GetMapping("/")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Health check endpoint at /health path
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

}

