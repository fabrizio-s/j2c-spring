package com.j2c.j2c.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.j2c.j2c.domain.entity")
@SpringBootApplication
public class DataJpaTestApplication {

	public static void main(final String[] args) {
		SpringApplication.run(DataJpaTestApplication.class, args);
	}

}
