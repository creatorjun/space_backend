package com.space.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpaceBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpaceBackendApplication.class, args);
    }
}
