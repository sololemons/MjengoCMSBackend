package com.storekeeperservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StorekeeperserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorekeeperserviceApplication.class, args);
	}

}
