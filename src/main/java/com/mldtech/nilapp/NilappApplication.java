package com.mldtech.nilapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NilappApplication {

	public static void main(String[] args) {
		SpringApplication.run(NilappApplication.class, args);
	}

}
