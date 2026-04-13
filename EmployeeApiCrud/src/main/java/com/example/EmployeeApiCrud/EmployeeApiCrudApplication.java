package com.example.EmployeeApiCrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmployeeApiCrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeApiCrudApplication.class, args);
	}

}
