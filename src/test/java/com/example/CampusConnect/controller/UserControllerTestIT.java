package com.example.CampusConnect.controller;

import com.example.CampusConnect.config.ContainersTestConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(ContainersTestConfiguration.class)
class UserControllerTestIT {

}
