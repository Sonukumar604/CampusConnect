package com.example.CampusConnect;


import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

class DockerDiagTest {

    @Test
    void printEnvAndDockerInfo() throws Exception {
        System.out.println("JVM PID: " + ProcessHandle.current().pid());
        System.out.println("DOCKER_HOST (env): " + System.getenv("DOCKER_HOST"));

        // Try to run 'docker info' from the same process that runs tests
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "info");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String out = new BufferedReader(new InputStreamReader(p.getInputStream()))
                    .lines().collect(Collectors.joining(System.lineSeparator()));
            int exit = p.waitFor();
            System.out.println("docker info exit=" + exit);
            System.out.println(out);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // Print Testcontainers detection attempt (non-invasive)
        try {
            System.out.println("Testcontainers version: " + DockerClientFactory.instance().getInfo());
        } catch (Throwable t) {
            System.out.println("Testcontainers client check failed:");
            t.printStackTrace();
        }
    }
}