package com.team7.rupiapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RupiappApplication {

	public static void main(String[] args) {
		SpringApplication.run(RupiappApplication.class, args);
	}

}
