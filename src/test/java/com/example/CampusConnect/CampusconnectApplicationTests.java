package com.example.CampusConnect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.CampusConnect.config.ContainersTestConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@Import(ContainersTestConfiguration.class)
class CampusconnectApplicationTests {

	@Test
	void contextLoads() {
	}

}
