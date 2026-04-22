package com.giapha.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class GiaphaApiApplication {

	public static void main(String[] args) {
		loadEnv();
		SpringApplication.run(GiaphaApiApplication.class, args);
	}

	private static void loadEnv() {
		try {
			Path path = Paths.get(".env");
			if (Files.exists(path)) {
				List<String> lines = Files.readAllLines(path);
				for (String line : lines) {
					if (line.contains("=") && !line.trim().startsWith("#")) {
						String[] parts = line.split("=", 2);
						String key = parts[0].trim();
						String value = parts[1].trim();
						System.setProperty(key, value);
					}
				}
				System.out.println(".env file loaded successfully");
			}
		} catch (IOException e) {
			System.err.println("Could not load .env file: " + e.getMessage());
		}
	}

}
