package com.lp3.osapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories

public class OsappApplication {

	public static void main(String[] args) {
		SpringApplication.run(OsappApplication.class, args);
	}

}
