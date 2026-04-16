package com.example.CampusConnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class CampusconnectApplication {

	public static void main(String[] args) {

        System.out.println("GOOGLE_CLIENT_ID = " + System.getenv("GOOGLE_CLIENT_ID"));
        System.out.println("GITHUB_CLIENT_ID = " + System.getenv("GITHUB_CLIENT_ID"));

        SpringApplication.run(CampusconnectApplication.class, args);
	}


}
