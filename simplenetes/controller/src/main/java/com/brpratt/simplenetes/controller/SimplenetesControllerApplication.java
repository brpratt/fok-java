package com.brpratt.simplenetes.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimplenetesControllerApplication {
    public static void main(String[] args) {
		SpringApplication.run(SimplenetesControllerApplication.class, args);
	}
}
