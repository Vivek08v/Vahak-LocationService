package com.example.vahaklocationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.kafka.annotation.EnableKafka;

@EntityScan("com.example.vahakentityservice.models")
@EnableKafka
@SpringBootApplication
public class VahakLocationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VahakLocationServiceApplication.class, args);
	}

}
