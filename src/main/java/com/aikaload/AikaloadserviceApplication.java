package com.aikaload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AikaloadserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AikaloadserviceApplication.class, args);
	 }

}
