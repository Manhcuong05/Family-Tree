package com.giapha.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GiaphaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiaphaApiApplication.class, args);
	}

}
